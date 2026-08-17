package com.example.timeline;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.List;

public class TimelineApp extends Application {
    private static final double YEAR_WIDTH = 8.0;
    private static final int ROWS = 5;
    private static final double ROW_HEIGHT = 48;
    private static final double LABEL_WIDTH = 125;

    @Override
    public void start(Stage stage) {
        List<Monarch> monarchs = List.of(
                new Monarch("William I", 1066, 1087),
                new Monarch("William II", 1087, 1100),
                new Monarch("Henry I", 1100, 1135),
            new Monarch("Stephen", 1135, 1154),
            new Monarch("Henry II", 1154, 1189),
            new Monarch("Richard I", 1189, 1199),
            new Monarch("John", 1199, 1216),
            new Monarch("Henry III", 1216, 1272),
            new Monarch("Edward I", 1272, 1307),
            new Monarch("Edward II", 1307, 1327),
            new Monarch("Edward III", 1327, 1377),
            new Monarch("Richard II", 1377, 1399),
            new Monarch("Henry IV", 1399, 1413),
            new Monarch("Henry V", 1413, 1422),
            new Monarch("Henry VI", 1422, 1461),
                new Monarch("Edward IV", 1461, 1483),
                new Monarch("Edward V", 1483, 1483),
                new Monarch("Richard III", 1483, 1485),
                new Monarch("Henry VII", 1485, 1509),
                new Monarch("Henry VIII", 1509, 1547),
                new Monarch("Edward VI", 1547, 1553),
                new Monarch("Mary I", 1553, 1558),
                new Monarch("Elizabeth I", 1558, 1603),
                new Monarch("James I", 1603, 1625),
                new Monarch("Charles I", 1625, 1649),
                new Monarch("Charles II", 1660, 1685),
                new Monarch("James II", 1685, 1688),
                new Monarch("William III", 1689, 1702),
                new Monarch("Mary II", 1689, 1694),
                new Monarch("Anne", 1702, 1707),
                new Monarch("George I", 1714, 1727),
                new Monarch("George II", 1727, 1760),
                new Monarch("George III", 1760, 1820),
                new Monarch("George IV", 1820, 1830),
                new Monarch("William IV", 1830, 1837),
                new Monarch("Victoria", 1837, 1901),

                new Monarch("Edward VII", 1901, 1910),
                new Monarch("George V", 1910, 1936),
                new Monarch("Edward VIII", 1936, 1936),
                new Monarch("George VI", 1936, 1952),
                new Monarch("Elizabeth II", 1952, 2022),
                new Monarch("Charles III", 2022, 2026)
        );

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        Label title = new Label("English Monarchs");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label hint = new Label("Five reusable rows • horizontal scroll • drag the scrollbar to explore the timeline");
        hint.setStyle("-fx-text-fill: #666;");

        VBox header = new VBox(4, title, hint);
        header.setPadding(new Insets(0, 0, 12, 0));

        Pane timeline = buildTimeline(monarchs);

        ScrollPane scroll = new ScrollPane(timeline);
        scroll.setPannable(true);
        scroll.setFitToHeight(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        root.setTop(header);
        root.setCenter(scroll);

        stage.setTitle("Historical Timeline Prototype");
        stage.setScene(new Scene(root, 1100, 420));
        stage.show();
    }

    private Pane buildTimeline(List<Monarch> monarchs) {
        int minYear = monarchs.stream().mapToInt(Monarch::startYear).min().orElse(1000);
        int maxYear = monarchs.stream().mapToInt(Monarch::endYear).max().orElse(1500);

        double chartWidth = (maxYear - minYear + 20) * YEAR_WIDTH;
        double totalWidth = LABEL_WIDTH + chartWidth;
        double height = 70 + ROWS * ROW_HEIGHT;

        Pane canvas = new Pane();
        canvas.setPrefSize(totalWidth, height);
        canvas.setMinSize(totalWidth, height);

        // Year grid and labels.
        for (int year = (minYear / 10) * 10; year <= maxYear + 10; year += 10) {
            double x = LABEL_WIDTH + (year - minYear) * YEAR_WIDTH;
            Line line = new Line(x, 30, x, height);
            line.setStroke(Color.LIGHTGRAY);
            canvas.getChildren().add(line);

            Label label = new Label(Integer.toString(year));
            label.setLayoutX(x + 2);
            label.setLayoutY(5);
            label.setStyle("-fx-font-size: 11px;");
            canvas.getChildren().add(label);
        }

        // Row labels and horizontal guides.
        for (int row = 0; row < ROWS; row++) {
            double y = 55 + row * ROW_HEIGHT;
            Line guide = new Line(0, y + ROW_HEIGHT - 5, totalWidth, y + ROW_HEIGHT - 5);
            guide.setStroke(Color.web("#dddddd"));
            canvas.getChildren().add(guide);

            Label rowLabel = new Label("Row " + (row + 1));
            rowLabel.setLayoutX(8);
            rowLabel.setLayoutY(y + 12);
            rowLabel.setPrefWidth(LABEL_WIDTH - 20);
            rowLabel.setStyle("-fx-text-fill: #777;");
            canvas.getChildren().add(rowLabel);
        }

        // Automatically reuse rows when possible.
        int[] rowEnd = new int[ROWS];
        for (int i = 0; i < ROWS; i++) rowEnd[i] = Integer.MIN_VALUE;

        int i = 0;
        for (Monarch monarch : monarchs) {
            //int row = findAvailableRow(rowEnd, monarch.startYear());
            int row = i % 3;
            rowEnd[row] = monarch.endYear();

            double x = LABEL_WIDTH + (monarch.startYear() - minYear) * YEAR_WIDTH;
            double width = Math.max(20, (monarch.endYear() - monarch.startYear()) * YEAR_WIDTH);
            double y = 55 + row * ROW_HEIGHT + 8;

            Rectangle bar = new Rectangle(x, y, width, 28);
            bar.setArcWidth(8);
            bar.setArcHeight(8);
            bar.setFill(Color.STEELBLUE);

            Label name = new Label(monarch.name());
            name.setTextFill(Color.WHITE);
            name.setAlignment(Pos.CENTER_LEFT);
            name.setPadding(new Insets(0, 6, 0, 6));
            name.setLayoutX(x);
            name.setLayoutY(y);
            name.setPrefHeight(28);
            name.setPrefWidth(width);
            name.setStyle("-fx-font-weight: bold;");

            String tooltipText = monarch.name() + " (" + monarch.startYear() + "–" + monarch.endYear() + ")";
            javafx.scene.control.Tooltip.install(bar, new javafx.scene.control.Tooltip(tooltipText));
            javafx.scene.control.Tooltip.install(name, new javafx.scene.control.Tooltip(tooltipText));

            canvas.getChildren().addAll(bar, name);
            i++;
        }

        return canvas;
    }

    private int findAvailableRow(int[] rowEnd, int startYear) {
        for (int row = 0; row < rowEnd.length; row++) {
            if (rowEnd[row] <= startYear) return row;
        }
        // Five rows are deliberately fixed for this prototype.
        return 0;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
