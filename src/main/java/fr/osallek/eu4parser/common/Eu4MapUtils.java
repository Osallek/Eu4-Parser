package fr.osallek.eu4parser.common;

import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.Province;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Eu4MapUtils {

    private Eu4MapUtils() {}

    private static final Logger LOGGER = LoggerFactory.getLogger(Eu4MapUtils.class);

    public static final Color EMPTY_COLOR = new Color(148, 146, 149);

    public static final Color OCEAN_COLOR = new Color(68, 107, 163);

    public static final Color IMPASSABLE_COLOR = new Color(94, 94, 94);

    public static Color winterToColor(String winter) {
        if ("mild_winter".equals(winter)) {
            return new Color(102, 102, 102);
        } else if ("normal_winter".equals(winter)) {
            return new Color(204, 204, 204);
        } else if ("severe_winter".equals(winter)) {
            return new Color(255, 255, 255);
        } else if (Eu4Utils.DEFAULT_WINTER.equals(winter)) {
            return new Color(34, 34, 34);
        } else {
            return EMPTY_COLOR;
        }
    }

    public static Color climateToColor(String climate) {
        if ("tropical".equals(climate)) {
            return new Color(116, 218, 81);
        } else if ("arid".equals(climate)) {
            return new Color(255, 240, 125);
        } else if ("arctic".equals(climate)) {
            return new Color(255, 255, 255);
        } else if (Eu4Utils.DEFAULT_CLIMATE.equals(climate)) {
            return new Color(76, 134, 71);
        } else if (Eu4Utils.IMPASSABLE_CLIMATE.equals(climate)) {
            return IMPASSABLE_COLOR;
        } else {
            return EMPTY_COLOR;
        }
    }

    public static Color monsoonToColor(String monsoon) {
        if ("mild_monsoon".equals(monsoon)) {
            return new Color(102, 102, 102);
        } else if ("normal_monsoon".equals(monsoon)) {
            return new Color(204, 204, 204);
        } else if ("severe_monsoon".equals(monsoon)) {
            return new Color(255, 255, 255);
        } else if (Eu4Utils.DEFAULT_MONSOON.equals(monsoon)) {
            return new Color(34, 34, 34);
        } else {
            return EMPTY_COLOR;
        }
    }

    public static void generateMapPng(Game game, File file, Function<Province, Color> provinceColorFunction) throws IOException {
        BufferedImage pngMapImage = generateMapPng(game, provinceColorFunction);
        ImageIO.write(pngMapImage, "PNG", file);
    }

    public static BufferedImage generateMapPng(Game game, Function<Province, Color> provinceColorFunction) throws IOException {
        Map<Province, Map<Polygon, Boolean>> borders = game.getBorders();
        BufferedImage pngMapImage = new BufferedImage(game.getProvinceImageWidth(), game.getProvinceImageHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D pngMapGraphics = pngMapImage.createGraphics();

        borders.forEach((province, polygons) -> {
            pngMapGraphics.setColor(provinceColorFunction.apply(province));
            polygons.keySet().forEach(pngMapGraphics::fillPolygon);
            pngMapGraphics.setColor(Color.BLACK);

            polygons.forEach((polygon, contained) -> {
                if (BooleanUtils.isTrue(contained)) {
                    pngMapGraphics.setStroke(new BasicStroke(1.5f));
                } else {
                    pngMapGraphics.setStroke(new BasicStroke(1f));
                }

                pngMapGraphics.drawPolygon(polygon);
            });

            pngMapGraphics.setStroke(new BasicStroke(1f));
        });

        return pngMapImage;
    }

    /**
     * @return Map<Province, Map < Border, Contained>>
     */
    public static Map<Province, Map<Polygon, Boolean>> imageToBorders(Game game, BufferedImage image) {
        int[] colors = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        int color = 0;

        Map<Province, List<Polygon>> borders = new HashMap<>();
        game.getProvinces().forEach((id, province) -> borders.put(province, new ArrayList<>()));

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (color != (color = colors[y * image.getWidth() + x]) || x == 0) {
                    int finalX = x;
                    int finalY = y;

                    if (borders.get(game.getProvincesByColor().get(new Color(color >> 16 & 0xFF, (color >> 8) & 0xFF, color & 0xFF).getRGB()))
                               .stream().noneMatch(polygon -> polygonContains(polygon, finalX, finalY))) {
                        borders.get(game.getProvincesByColor().get(new Color(color >> 16 & 0xFF, (color >> 8) & 0xFF, color & 0xFF).getRGB()))
                               .add(generatePath(colors, x, y, image.getWidth(), image.getHeight()));
                    }
                }
            }
        }

        borders.values().forEach(polygons -> polygons.removeIf(polygon -> polygon.npoints <= 0));
        borders.values().removeIf(CollectionUtils::isEmpty);

        List<Polygon> listBorders = borders.values().stream().flatMap(Collection::stream).toList();

        //Sort to have "classic" provinces last for drawing purposes
        return borders.entrySet()
                      .stream()
                      .sorted((o1, o2) -> Comparator.comparing(Province::isOcean, Comparator.reverseOrder())
                                                    .thenComparing(Province::isImpassable)
                                                    .thenComparing(Province::isLake)
                                                    .compare(o1.getKey(), o2.getKey()))
                      .collect(Collectors.toMap(Map.Entry::getKey,
                                                entry -> entry.getValue()
                                                              .stream()
                                                              .collect(Collectors.toMap(Function.identity(),
                                                                                        polygon -> listBorders.stream()
                                                                                                              .anyMatch(p -> p.contains(polygon.getBounds())
                                                                                                                             || isInsidePolygon(p, polygon)))),
                                                (a, b) -> a, LinkedHashMap::new));
    }

    private static boolean sameColor(int rgb, int[] colors, int x, int y, int width, int height) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        } else {
            return rgb == colors[y * width + x];
        }
    }

    private static Polygon generatePath(int[] colors, int x, int y, int width, int height) {
        int color;
        int startX = x;
        int startY = y;
        Direction direction = Direction.SAME;
        Direction prevDirection;
        Polygon polygon = new Polygon();

        do {
            color = colors[y * width + x];

            switch (direction) {
                case SAME, RIGHT:
                    if (sameColor(color, colors, x, y - 1, width, height)) {
                        prevDirection = direction;
                        direction = Direction.UP;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        y--;

                    } else if (sameColor(color, colors, x + 1, y, width, height)) {
                        prevDirection = direction;
                        direction = Direction.RIGHT;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        x++;
                    } else if (sameColor(color, colors, x, y + 1, width, height)) {
                        prevDirection = direction;
                        direction = Direction.DOWN;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        y++;
                    } else if (sameColor(color, colors, x - 1, y, width, height)) {
                        prevDirection = direction;
                        direction = Direction.LEFT;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        x--;
                    } else {
                        prevDirection = direction;
                        direction = Direction.SAME;
                    }

                    break;
                case DOWN:
                    if (sameColor(color, colors, x + 1, y, width, height)) {
                        prevDirection = direction;
                        direction = Direction.RIGHT;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        x++;
                    } else if (sameColor(color, colors, x, y + 1, width, height)) {
                        prevDirection = direction;
                        direction = Direction.DOWN;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        y++;
                    } else if (sameColor(color, colors, x - 1, y, width, height)) {
                        prevDirection = direction;
                        direction = Direction.LEFT;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        x--;
                    } else if (sameColor(color, colors, x, y - 1, width, height)) {
                        prevDirection = direction;
                        direction = Direction.UP;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        y--;
                    } else {
                        prevDirection = direction;
                        direction = Direction.SAME;
                    }

                    break;
                case LEFT:
                    if (sameColor(color, colors, x, y + 1, width, height)) {
                        prevDirection = direction;
                        direction = Direction.DOWN;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        y++;
                    } else if (sameColor(color, colors, x - 1, y, width, height)) {
                        prevDirection = direction;
                        direction = Direction.LEFT;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        x--;
                    } else if (sameColor(color, colors, x, y - 1, width, height)) {
                        prevDirection = direction;
                        direction = Direction.UP;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        y--;
                    } else if (sameColor(color, colors, x + 1, y, width, height)) {
                        prevDirection = direction;
                        direction = Direction.RIGHT;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        x++;
                    } else {
                        prevDirection = direction;
                        direction = Direction.SAME;
                    }

                    break;
                case UP:
                    if (sameColor(color, colors, x - 1, y, width, height)) {
                        prevDirection = direction;
                        direction = Direction.LEFT;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        x--;
                    } else if (sameColor(color, colors, x, y - 1, width, height)) {
                        prevDirection = direction;
                        direction = Direction.UP;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        y--;
                    } else if (sameColor(color, colors, x + 1, y, width, height)) {
                        prevDirection = direction;
                        direction = Direction.RIGHT;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        x++;
                    } else if (sameColor(color, colors, x, y + 1, width, height)) {
                        prevDirection = direction;
                        direction = Direction.DOWN;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        y++;
                    } else {
                        prevDirection = direction;
                        direction = Direction.SAME;
                    }

                    break;
            }
        } while (x != startX || y != startY);

        return polygon;
    }

    private static boolean polygonContains(Polygon polygon, int x, int y) {
        if (polygon.npoints <= 2 || !rectangleContains(polygon.getBounds(), x, y)) {
            return false;
        }

        int curx;
        int cury;

        for (int i = 0; i < polygon.npoints; i++) {
            curx = polygon.xpoints[i];
            cury = polygon.ypoints[i];

            if (x == curx && y == cury) {
                return true;
            }
        }


        for (int i = 0; i < polygon.npoints; i++) {
            int startX = polygon.xpoints[i];
            int startY = polygon.ypoints[i];
            int endX = polygon.xpoints[(i == polygon.npoints - 1) ? 0 : i + 1];
            int endY = polygon.ypoints[(i == polygon.npoints - 1) ? 0 : i + 1];

            if (startX == endX && startX == x) {
                if (startY == endY && startY == y) {
                    return true;
                } else if (startY < endY && startY <= y && endY >= y) {
                    return true;
                } else if (startY > endY && startY >= y && endY <= y) {
                    return true;
                }
            } else if (startY == endY && startY == y) {
                if (startX < endX && startX <= x && endX >= x) {
                    return true;
                } else if (startX > endX && startX >= x && endX <= x) {
                    return true;
                }
            }
        }

        return polygon.contains(x, y);
    }

    private static boolean rectangleContains(Rectangle rectangle, int x, int y) {
        int w = rectangle.width;
        int h = rectangle.height;

        if ((w | h) < 0) {
            return false;
        }

        int startX = rectangle.x;
        int startY = rectangle.y;

        if (x < startX || y < startY) {
            return false;
        }

        w += startX;
        h += startY;

        return ((w < startX || w >= x) && (h < startY || h >= y));
    }

    private static boolean isInsidePolygon(Polygon polygon1, Polygon polygon2) {
        int[] xpoints = polygon2.xpoints;
        int[] ypoints = polygon2.ypoints;
        boolean result = true;

        for (int i = 0, j = 0; i < polygon2.npoints; i++, j++) {
            result = polygon1.contains(xpoints[i], ypoints[j]);

            if (!result) {
                break;
            }
        }
        return result;
    }
}
