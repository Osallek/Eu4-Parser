package fr.osallek.eu4parser.common;

import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.Province;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGPolygon;
import org.apache.batik.util.CSSConstants;
import org.apache.batik.util.SVGConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
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

    public static final Color EMPTY_COLOR = new Color(148, 146, 149);

    public static final Color OCEAN_COLOR = new Color(68, 107, 163);

    public static final Color IMPASSABLE_COLOR = new Color(94, 94, 94);

    public static void generateMapSVG(Game game, File file) throws IOException {
        BufferedImage image = ImageIO.read(new File(game.getProvincesImage().getAbsolutePath()));
        Map<Province, Map<Polygon, Boolean>> borders = game.getBorders();

        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument("http://www.w3.org/2000/svg", "svg", null);
        SVGGeneratorContext generatorContext = SVGGeneratorContext.createDefault(document);
        generatorContext.setComment(null);
        SVGGraphics2D svgGenerator = new SVGGraphics2D(generatorContext, false);
        SVGPolygon svgPath = new SVGPolygon(generatorContext);
        Element rootElement = document.getDocumentElement();
        rootElement.setAttributeNS(null, SVGConstants.SVG_WIDTH_ATTRIBUTE, String.valueOf(image.getWidth()));
        rootElement.setAttributeNS(null, SVGConstants.SVG_HEIGHT_ATTRIBUTE, String.valueOf(image.getHeight()));
        rootElement.setAttributeNS(null, SVGConstants.SVG_VIEW_BOX_ATTRIBUTE, "0 0 " + image.getWidth() + " " + image.getHeight());
        rootElement.setAttributeNS(null, SVGConstants.SVG_FILL_ATTRIBUTE, "#949295");
        rootElement.setAttributeNS(null, SVGConstants.SVG_STROKE_WIDTH_ATTRIBUTE, "1");
        rootElement.setAttributeNS(null, SVGConstants.SVG_FILL_OPACITY_ATTRIBUTE, "1");
        rootElement.setAttributeNS(null, SVGConstants.SVG_STROKE_ATTRIBUTE, "black");
        rootElement.setAttributeNS(null, SVGConstants.SVG_STROKE_LINECAP_ATTRIBUTE, SVGConstants.SVG_SQUARE_VALUE);

        Element rectBackground = document.createElementNS(SVGConstants.SVG_NAMESPACE_URI, SVGConstants.SVG_RECT_TAG);
        rectBackground.setAttributeNS(null, SVGConstants.SVG_WIDTH_ATTRIBUTE, String.valueOf(image.getWidth()));
        rectBackground.setAttributeNS(null, SVGConstants.SVG_HEIGHT_ATTRIBUTE, String.valueOf(image.getHeight()));
        rectBackground.setAttributeNS(null, SVGConstants.SVG_FILL_ATTRIBUTE, "black");
        rectBackground.setAttributeNS(null, SVGConstants.SVG_X_ATTRIBUTE, "0");
        rectBackground.setAttributeNS(null, SVGConstants.SVG_Y_ATTRIBUTE, "0");

        rootElement.appendChild(rectBackground);

        borders.forEach((province, value) -> value.forEach((polygon, contained) -> {
            Element element = svgPath.toSVG(polygon);
            element.setAttributeNS(null, "data-pid", String.valueOf(province.getId()));

            //Check if the province in fully inside another to add double sized borders
            if (BooleanUtils.isTrue(contained)) {
                element.setAttributeNS(null, SVGConstants.SVG_STROKE_WIDTH_ATTRIBUTE, "1.5");
            }

            if (province.isOcean() || province.isLake()) {
                element.setAttributeNS(null, CSSConstants.CSS_FILL_PROPERTY, "#446ba3");
            } else if (province.isImpassable()) {
                element.setAttributeNS(null, CSSConstants.CSS_FILL_PROPERTY, "#5e5e5e");
            }

            rootElement.appendChild(element);
        }));

        try (FileWriter writer = new FileWriter(file)) {
            svgGenerator.stream(rootElement, writer);
        }
    }

    public static void generateMapPng(Game game, File file, Function<Province, Color> provinceColorFunction) throws IOException {
        BufferedImage pngMapImage = generateMapPng(game, provinceColorFunction);
        ImageIO.write(pngMapImage, "PNG", file);
    }

    public static BufferedImage generateMapPng(Game game, Function<Province, Color> provinceColorFunction) throws IOException {
        BufferedImage provinceImage = ImageIO.read(new File(game.getProvincesImage().getAbsolutePath()));
        Map<Province, Map<Polygon, Boolean>> borders = game.getBorders();
        BufferedImage pngMapImage = new BufferedImage(provinceImage.getWidth(), provinceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
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

                    if (borders.get(game.getProvinceByColor((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF))
                               .stream().noneMatch(polygon -> polygonContains(polygon, finalX, finalY))) {
                        borders.get(game.getProvinceByColor((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF))
                               .add(generatePath(colors, x, y, image.getWidth(), image.getHeight()));
                    }
                }
            }
        }

        borders.values().forEach(polygons -> polygons.removeIf(polygon -> polygon.npoints <= 0));
        borders.values().removeIf(CollectionUtils::isEmpty);

        List<Polygon> listBorders = borders.values().stream().flatMap(Collection::stream).collect(Collectors.toList());

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
        int direction = -1; //-1 = same position, 0 = right, 1 = bottom, 2 = left, 3 = top
        int prevDirection = -1;
        Polygon polygon = new Polygon();

        do {
            color = colors[y * width + x];

            switch (direction) {
                case -1, 0:
                    if (sameColor(color, colors, x, y - 1, width, height)) {
                        prevDirection = direction;
                        direction = 3;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        y--;

                    } else if (sameColor(color, colors, x + 1, y, width, height)) {
                        prevDirection = direction;
                        direction = 0;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        x++;
                    } else if (sameColor(color, colors, x, y + 1, width, height)) {
                        prevDirection = direction;
                        direction = 1;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        y++;
                    } else if (sameColor(color, colors, x - 1, y, width, height)) {
                        prevDirection = direction;
                        direction = 2;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        x--;
                    } else {
                        prevDirection = direction;
                        direction = -1;
                    }

                    break;
                case 1:
                    if (sameColor(color, colors, x + 1, y, width, height)) {
                        prevDirection = direction;
                        direction = 0;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        x++;
                    } else if (sameColor(color, colors, x, y + 1, width, height)) {
                        prevDirection = direction;
                        direction = 1;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        y++;
                    } else if (sameColor(color, colors, x - 1, y, width, height)) {
                        prevDirection = direction;
                        direction = 2;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        x--;
                    } else if (sameColor(color, colors, x, y - 1, width, height)) {
                        prevDirection = direction;
                        direction = 3;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        y--;
                    } else {
                        prevDirection = direction;
                        direction = -1;
                    }

                    break;
                case 2:
                    if (sameColor(color, colors, x, y + 1, width, height)) {
                        prevDirection = direction;
                        direction = 1;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        y++;
                    } else if (sameColor(color, colors, x - 1, y, width, height)) {
                        prevDirection = direction;
                        direction = 2;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        x--;
                    } else if (sameColor(color, colors, x, y - 1, width, height)) {
                        prevDirection = direction;
                        direction = 3;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        y--;
                    } else if (sameColor(color, colors, x + 1, y, width, height)) {
                        prevDirection = direction;
                        direction = 0;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        x++;
                    } else {
                        prevDirection = direction;
                        direction = -1;
                    }

                    break;
                case 3:
                    if (sameColor(color, colors, x - 1, y, width, height)) {
                        prevDirection = direction;
                        direction = 2;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        x--;
                    } else if (sameColor(color, colors, x, y - 1, width, height)) {
                        prevDirection = direction;
                        direction = 3;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        y--;
                    } else if (sameColor(color, colors, x + 1, y, width, height)) {
                        prevDirection = direction;
                        direction = 0;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        x++;
                    } else if (sameColor(color, colors, x, y + 1, width, height)) {
                        prevDirection = direction;
                        direction = 1;

                        if (direction != prevDirection) {
                            polygon.addPoint(x, y);
                        }

                        y++;
                    } else {
                        prevDirection = direction;
                        direction = -1;
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
