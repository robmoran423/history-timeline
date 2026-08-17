package com.example.timeline;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TimelineApp extends Application {
    private static final double YEAR_WIDTH = 8.0;
    private static final int ROWS = 5;
    private static final double ROW_HEIGHT = 48;
    private static final double LABEL_WIDTH = 125;

    @Override
    public void start(Stage stage) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("english-monarch.json");
        List<Monarch> monarchs = mapper.readValue(is, new TypeReference<List<Monarch>>() {});

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
