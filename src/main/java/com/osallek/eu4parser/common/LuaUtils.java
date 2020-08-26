package com.osallek.eu4parser.common;

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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class LuaUtils {

    private LuaUtils() {}

    public static Map<String, Map<String, Exp.Constant>> luaFileToMap(String file) throws FileNotFoundException, ParseException {
        LuaParser luaParser = new LuaParser(new FileInputStream(file));

        Chunk chunk = luaParser.Chunk();
        TableConstructor tableConstructor = (TableConstructor) ((Stat.Assign) chunk.block.stats.get(0)).exps.get(0);
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
}
