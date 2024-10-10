package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java Main <values.json> <tests.json> <report.json>");
            return;
        }

        String valuesPath = args[0];
        String testsPath = args[1];
        String reportPath = args[2];

        ObjectMapper mapper = new ObjectMapper();

        try {
            // Чтение values.json
            JsonNode valuesNode = mapper.readTree(new File(valuesPath));
            Map<Integer, String> valuesMap = new HashMap<>();
            for (JsonNode value : valuesNode.get("values")) {
                valuesMap.put(value.get("id").asInt(), value.get("value").asText());
            }

            // Чтение tests.json
            JsonNode testsNode = mapper.readTree(new File(testsPath));

            // Заполнение значений в структуре tests.json
            fillValues(testsNode, valuesMap);

            // Запись в report.json
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(reportPath), testsNode);

            System.out.println("Отчет успешно создан!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void fillValues(JsonNode node, Map<Integer, String> valuesMap) {
        if (node.isArray()) {
            for (JsonNode item : node) {
                fillValues(item, valuesMap);
            }
        } else if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            if (objectNode.has("id") && objectNode.has("value")) {
                int id = objectNode.get("id").asInt();
                if (valuesMap.containsKey(id)) {
                    objectNode.put("value", valuesMap.get(id));
                }
            }
            if (objectNode.has("values")) {
                fillValues(objectNode.get("values"), valuesMap);
            }
        }
    }
}
