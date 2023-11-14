package com.nonames.cli.utils;

import java.util.*;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

public class PrintUtils {

    public static String[][] convertGetParticipantsResponseToTable(JSONArray jsonArray) {
        List<List<String>> data = new ArrayList<>();
        data.add(Arrays.asList("ID", "Name", "Email"));

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject participant = jsonArray.getJSONObject(i);
            List<String> currentRow = new ArrayList<>(Arrays.asList(participant.getString("id"),
                    participant.getString("name"), participant.getString("email")));
            data.add(currentRow);
        }
        return data.stream()
                    .map(l -> l.toArray(String[]::new))
                    .toArray(String[][]::new);
    }

    public static String[][] convertGetEventsResponseToTable(JSONArray jsonArray) {
        List<List<String>> data = new ArrayList<>();
        data.add(Arrays.asList("ID", "Date", "Time", "Title", "Description", "Host Email", "Participants"));

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject event = jsonArray.getJSONObject(i);
            List<String> currentRow = new ArrayList<>(
                    Arrays.asList(event.getString("id"), event.getString("date"), event.getString("time"),
                            event.getString("title"), event.getString("description"), event.getString("hostEmail")));

            JSONArray participantsJSON = event.getJSONArray("participants");
            if (participantsJSON.isEmpty()) {
                currentRow.add("None");
            } else {
                StringBuilder participantListAggregateInfo = new StringBuilder();
                for (int j = 0; j < participantsJSON.length(); j++) {
                    JSONObject participant = participantsJSON.getJSONObject(j);

                    String participantInfo = String.format("(%d) Id: %s, Name: %s, Email: %s ",
                            j + 1, participant.getString("id"), participant.getString("name"),
                            participant.getString("email"));
                    participantListAggregateInfo.append(participantInfo);
                }
                currentRow.add(participantListAggregateInfo.toString());
            }
            data.add(currentRow);
        }
        return data.stream()
                .map(l -> l.toArray(String[]::new))
                .toArray(String[][]::new);
    }

    /**
     * Pretty prints a table with the specified data
     * ATTRIBUTION: <a href=
     * "https://itsallbinary.com/java-printing-to-console-in-table-format-simple-code-with-flexible-width-left-align-header-separator-line/">Code
     * adapted from here</a>
     * 
     * @param table
     */
    public static void prettyPrintTable(String[][] table) {
        boolean leftJustifiedRows = true;
        int maxWidth = 40;

        /*
         * Create new table array with wrapped rows
         */
        List<String[]> tableList = new ArrayList<>(Arrays.asList(table));
        List<String[]> finalTableList = new ArrayList<>();
        for (String[] row : tableList) {
            // If any cell data is more than max width, then it will need extra row.
            boolean needExtraRow = false;
            // Count of extra split row.
            int splitRow = 0;
            do {
                needExtraRow = false;
                String[] newRow = new String[row.length];
                for (int i = 0; i < row.length; i++) {
                    // If data is less than max width, use that as it is.
                    if (row[i].length() < maxWidth) {
                        newRow[i] = splitRow == 0 ? row[i] : "";
                    } else if ((row[i].length() > (splitRow * maxWidth))) {
                        // If data is more than max width, then crop data at maxwidth.
                        // Remaining cropped data will be part of next row.
                        int end = row[i].length() > ((splitRow * maxWidth) + maxWidth)
                                ? (splitRow * maxWidth) + maxWidth
                                : row[i].length();
                        newRow[i] = row[i].substring((splitRow * maxWidth), end);
                        needExtraRow = true;
                    } else {
                        newRow[i] = "";
                    }
                }
                finalTableList.add(newRow);
                if (needExtraRow) {
                    splitRow++;
                }
            } while (needExtraRow);
        }
        String[][] finalTable = new String[finalTableList.size()][finalTableList.get(0).length];
        for (int i = 0; i < finalTable.length; i++) {
            finalTable[i] = finalTableList.get(i);
        }

        Map<Integer, Integer> columnLengths = new HashMap<>();
        Arrays.stream(finalTable).forEach(a -> Stream.iterate(0, (i -> i < a.length), (i -> ++i)).forEach(i -> {
            if (columnLengths.get(i) == null) {
                columnLengths.put(i, 0);
            }
            if (columnLengths.get(i) < a[i].length()) {
                columnLengths.put(i, a[i].length());
            }
        }));

        final StringBuilder formatString = new StringBuilder("");
        String flag = leftJustifiedRows ? "-" : "";
        columnLengths.entrySet().stream().forEach(e -> formatString.append("| %" + flag + e.getValue() + "s "));
        formatString.append("|\n");

        /*
         * Prepare line for top, bottom & below header row.
         */
        String line = columnLengths.entrySet().stream().reduce("", (ln, b) -> {
            String templn = "+-";
            templn = templn + Stream.iterate(0, (i -> i < b.getValue()), (i -> ++i)).reduce("", (ln1, b1) -> ln1 + "-",
                    (a1, b1) -> a1 + b1);
            templn = templn + "-";
            return ln + templn;
        }, (a, b) -> a + b);
        line = line + "+\n";

        /*
         * Print table
         */
        System.out.print(line);
        Arrays.stream(finalTable).limit(1).forEach(a -> System.out.printf(formatString.toString(), a));
        System.out.print(line);

        Stream.iterate(1, (i -> i < finalTable.length), (i -> ++i))
                .forEach(a -> System.out.printf(formatString.toString(), finalTable[a]));
        System.out.print(line);
    }
}