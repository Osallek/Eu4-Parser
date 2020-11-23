package com.osallek.eu4parser.common;

import org.apache.commons.collections4.CollectionUtils;
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.ast.Chunk;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.Stat;
import org.luaj.vm2.ast.TableConstructor;
import org.luaj.vm2.ast.TableField;
import org.luaj.vm2.parser.LuaParser;
import org.luaj.vm2.parser.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class LuaUtils {

    private LuaUtils() {}

    public static Map<String, Map<String, Exp.Constant>> luaFileToMap(File file) throws FileNotFoundException, ParseException {
        if (file == null) {
            return new HashMap<>();
        }

        LuaParser luaParser = new LuaParser(new FileInputStream(file));

        Chunk chunk = luaParser.Chunk();

        if (chunk.block.stats.size() == 1) {
            return readFromTable((TableConstructor) ((Stat.Assign) chunk.block.stats.get(0)).exps.get(0));
        } else {
            return readFromList(chunk.block.stats);
        }
    }

    private static Map<String, Map<String, Exp.Constant>> readFromTable(TableConstructor tableConstructor) {
        return ((List<TableField>) tableConstructor.fields).stream().collect(Collectors.toMap(o -> o.name, o -> {
            TableConstructor constructor = (TableConstructor) o.rhs;
            return ((List<TableField>) constructor.fields).stream().collect(Collectors.toMap(field -> field.name, field -> {
                Exp.Constant constant;
                if (Exp.Constant.class.equals(field.rhs.getClass())) {
                    constant = (Exp.Constant) field.rhs;
                } else if (Exp.UnopExp.class.equals(field.rhs.getClass())) {
                    if (((Exp.UnopExp) field.rhs).op == 19) {
                        LuaNumber luaNumber = (LuaNumber) ((Exp.Constant) ((Exp.UnopExp) field.rhs).rhs).value;

                        if (LuaInteger.class.equals(luaNumber.getClass())) {
                            constant = new Exp.Constant(LuaValue.valueOf(-luaNumber.toint()));
                        } else if (LuaDouble.class.equals(luaNumber.getClass())) {
                            constant = new Exp.Constant(LuaValue.valueOf(-luaNumber.todouble()));
                        } else {
                            constant = new Exp.Constant(luaNumber);
                        }
                    } else {
                        constant = (Exp.Constant) ((Exp.UnopExp) field.rhs).rhs;
                    }
                } else {
                    constant = (Exp.Constant) field.rhs;
                }

                return constant;
            }, (constant, constant2) -> constant2));
        }));
    }

    private static Map<String, Map<String, Exp.Constant>> readFromList(List<Stat.Assign> list) {
        Map<String, Map<String, Exp.Constant>> map = new HashMap<>();

        list.forEach(assign -> {
            if (CollectionUtils.isNotEmpty(assign.exps) && CollectionUtils.isNotEmpty(assign.vars)) {
                Exp.Constant constant;
                if (Exp.Constant.class.equals(assign.exps.get(0).getClass())) {
                    constant = (Exp.Constant) assign.exps.get(0);
                } else if (Exp.UnopExp.class.equals(assign.exps.get(0).getClass())) {
                    if (((Exp.UnopExp) assign.exps.get(0)).op == 19) {
                        LuaNumber luaNumber = (LuaNumber) ((Exp.Constant) ((Exp.UnopExp) assign.exps.get(0)).rhs).value;

                        if (LuaInteger.class.equals(assign.exps.get(0).getClass())) {
                            constant = new Exp.Constant(LuaValue.valueOf(-luaNumber.toint()));
                        } else if (LuaDouble.class.equals(luaNumber.getClass())) {
                            constant = new Exp.Constant(LuaValue.valueOf(-luaNumber.todouble()));
                        } else {
                            constant = new Exp.Constant(luaNumber);
                        }
                    } else {
                        constant = (Exp.Constant) ((Exp.UnopExp) assign.exps.get(0)).rhs;
                    }
                } else {
                    constant = (Exp.Constant) assign.exps.get(0);
                }

                String name = null;
                String rootName = null;

                if (Exp.FieldExp.class.equals(assign.vars.get(0).getClass())) {
                    name = ((Exp.FieldExp) assign.vars.get(0)).name.name;

                    if (Exp.FieldExp.class.equals(((Exp.FieldExp) assign.vars.get(0)).lhs.getClass())) {
                        rootName = ((Exp.FieldExp)(((Exp.FieldExp) assign.vars.get(0)).lhs)).name.name;
                    }
                }

                if (constant != null && name != null && rootName != null) {
                    String finalName = name;
                    map.compute(rootName, (root, values) -> {
                        if (values == null) {
                            values = new HashMap<>();
                        }

                        values.put(finalName, constant);

                        return values;
                    });
                }
            }
        });

        return map;
    }
}
