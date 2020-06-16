/**
 *       Our Grammar So Far 
 * 
 *       expression → literal | unary | binary | grouping ;
 *       literal    → NUMBER | STRING | "true" | "false" | "nil" ;
 *       grouping   → "(" expression ")" ;
 *       unary      → ( "-" | "!" ) expression ;
 *       binary     → expression operator expression ;
 *       operator   → "==" | "!=" | "<" | "<=" | ">" | ">=" | "+"  | "-"  | "*" | "/" ;
 */

package com.craftinginterpreters.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }

        String outputDir = args[0];

        generateAst(outputDir, "Expr", Arrays.asList("Binary : Expr left, Token operator, Expr right",
                "Grouping : Expr expression", "Literal : Object value", "Unary : Token operator, Expr right"));
    }

    private static void generateAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package com.craftinginterpreters.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println(String.format("abstract class %s {", baseName));
        writer.println();

        // Abstract method
        writer.println("\tabstract <R> R accept(Visitor<R> visitor);");
        writer.println();

        // Generate visitor interface
        generateVisitor(writer, baseName, types);

        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            generateType(writer, baseName, className, fields);
        }

        writer.println("}");
        writer.close();
    }

    private static void generateType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println(String.format("\tstatic class %s extends %s {", className, baseName));

        String[] fieldNames = fieldList.split(", ");

        // Fields
        for (String field : fieldNames) {
            writer.println(String.format("\t\tfinal %s;", field.trim()));
        }

        writer.println();

        // Constructor
        writer.println(String.format("\t\t%s(%s) {", className, fieldList));

        for (String field : fieldNames) {
            String fieldName = field.split(" ")[1];
            writer.println(String.format("\t\t\tthis.%s = %s;", fieldName, fieldName));
        }

        writer.println("\t\t}");

        // Abstract method implementation
        writer.println();
        writer.println("\t\t@Override");
        writer.println("\t\t<R> R accept(Visitor<R> visitor) {");
        writer.println("\t\t\treturn visitor.visit(this);");
        writer.println("\t\t}");

        writer.println("\t}");
        writer.println();
    }

    private static void generateVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("\tinterface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            // writer.println("\tR visit" + typeName + baseName + "(" + typeName + " " +
            // baseName.toLowerCase() + ");");
            writer.println(String.format("\t\tR visit(%s %s);", typeName, typeName.toLowerCase()));
        }

        writer.println("\t}");
        writer.println();
    }
}