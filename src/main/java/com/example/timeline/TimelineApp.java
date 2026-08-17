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
import java.util.Comparator;
import java.util.List;

public class TimelineApp extends Application {
    private static final double YEAR_WIDTH = 8.0;
    private static final int ROWS = 13;
    private static final double ROW_HEIGHT = 48;
    private static final double LABEL_WIDTH = 125;
    private static final int MIN_YEAR = 1000;
    private static final int MAX_YEAR = 2030;

    @Override
    public void start(Stage stage) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("english-monarch.json");
        List<Monarch> englishMonarchs = mapper.readValue(is, new TypeReference<List<Monarch>>() {});

        is = getClass().getClassLoader().getResourceAsStream("french-monarch.json");
        List<Monarch> frenchMonarchs = mapper.readValue(is, new TypeReference<List<Monarch>>() {});

        is = getClass().getClassLoader().getResourceAsStream("other-reigns.json");
        List<Monarch> otherReigns = mapper.readValue(is, new TypeReference<List<Monarch>>() {});

        is = getClass().getClassLoader().getResourceAsStream("historical-figures.json");
        List<HistoricalFigure> historicalFigures = mapper.readValue(is, new TypeReference<List<HistoricalFigure>>() {});

        is = getClass().getClassLoader().getResourceAsStream("historical-events.json");
        List<HistoricalEvent> historicalEvents = mapper.readValue(is, new TypeReference<List<HistoricalEvent>>() {});

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        Label title = new Label("Historical Timeline");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label hint = new Label("Timeline of English Monarchs, Other Reigns, Historical Figures and Events");
        hint.setStyle("-fx-text-fill: #666;");

        VBox header = new VBox(4, title, hint);
        header.setPadding(new Insets(0, 0, 12, 0));

        Pane timeline = buildTimeline();

        plotMonarchs(englishMonarchs, "English Monarchs", 0, 2,timeline);
        plotMonarchs(frenchMonarchs, "French Monarchs", 2, 2,timeline);
        //plotOtherReigns(otherReigns, timeline);
        plotHistoricalFigures(historicalFigures, timeline);
        plotHistoricalEvents(historicalEvents, timeline);

        ScrollPane scroll = new ScrollPane(timeline);
        scroll.setPannable(true);
        scroll.setFitToHeight(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        root.setTop(header);
        root.setCenter(scroll);

        stage.setTitle("Historical Timeline Prototype");
        stage.setScene(new Scene(root, 1100, 800));
        stage.show();
    }

    private Pane buildTimeline() {
        double chartWidth = (MAX_YEAR - MIN_YEAR + 20) * YEAR_WIDTH;
        double totalWidth = LABEL_WIDTH + chartWidth;
        double height = 70 + ROWS * ROW_HEIGHT;

        Pane canvas = new Pane();
        canvas.setPrefSize(totalWidth, height);
        canvas.setMinSize(totalWidth, height);

        // Year grid and labels.
        for (int year = (MIN_YEAR / 10) * 10; year <= MAX_YEAR + 10; year += 10) {
            double x = LABEL_WIDTH + (year - MIN_YEAR) * YEAR_WIDTH;
            Line line = new Line(x, 30, x, height);
            line.setStroke(Color.LIGHTGRAY);
            canvas.getChildren().add(line);

            Label label = new Label(Integer.toString(year));
            label.setLayoutX(x + 2);
            label.setLayoutY(5);
            label.setStyle("-fx-font-size: 11px;");
            canvas.getChildren().add(label);
        }

        return canvas;
    }

    private static void addGroupLabel(int row, String label, Pane canvas) {
        double y = 55 + row * ROW_HEIGHT;

        Label rowLabel = new Label(label);
        rowLabel.setLayoutX(8);
        rowLabel.setLayoutY(y + 12);
        rowLabel.setPrefWidth(LABEL_WIDTH - 20);
        rowLabel.setStyle("-fx-text-fill: #777;");
        canvas.getChildren().add(rowLabel);
    }

    private static void addGroupLine(int row, Pane canvas) {
        double y = 55 + row * ROW_HEIGHT;
        double chartWidth = (MAX_YEAR - MIN_YEAR + 20) * YEAR_WIDTH;
        double totalWidth = LABEL_WIDTH + chartWidth;

        Line guide = new Line(0, y + ROW_HEIGHT - 5, totalWidth, y + ROW_HEIGHT - 5);
        guide.setStroke(Color.web("#dddddd"));
        canvas.getChildren().add(guide);
    }

    private static void plotMonarchs(List<Monarch> monarchs, String label, int startRow, int rowCount, Pane canvas) {
        int year = 0;

        addGroupLabel(startRow, label, canvas);
        addGroupLine(startRow + rowCount - 1, canvas);

        monarchs.sort(Comparator.comparing(Monarch::startYear)
                .thenComparing(Monarch::endYear)
                .thenComparing(Monarch::name));

        for (Monarch monarch : monarchs) {
            int row = startRow;
            if (year == monarch.startYear()) {
                row = row + 1;
            }
            year = monarch.startYear();

            double x = LABEL_WIDTH + (monarch.startYear() - MIN_YEAR) * YEAR_WIDTH;
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

            String tooltipText = monarch.name() + " (" + monarch.startYear() + "–" + monarch.endYear() + ") - " + monarch.category();
            javafx.scene.control.Tooltip.install(bar, new javafx.scene.control.Tooltip(tooltipText));
            javafx.scene.control.Tooltip.install(name, new javafx.scene.control.Tooltip(tooltipText));

            canvas.getChildren().addAll(bar, name);
        }
    }

    private static void plotHistoricalFigures(List<HistoricalFigure> historicalFigures, Pane canvas) {
        int i = 0;
        addGroupLabel(6, "Historical Figures", canvas);
        addGroupLine(11, canvas);

        historicalFigures.sort(Comparator.comparing(HistoricalFigure::birthYear)
                .thenComparing(HistoricalFigure::deathYear)
                .thenComparing(HistoricalFigure::name));

        for (HistoricalFigure historicalFigure : historicalFigures) {
            int row = (i % 6) + 6;

            double x = LABEL_WIDTH + (historicalFigure.birthYear() - MIN_YEAR) * YEAR_WIDTH;
            double width = Math.max(20, (historicalFigure.deathYear() - historicalFigure.birthYear()) * YEAR_WIDTH);
            double y = 55 + row * ROW_HEIGHT + 8;

            Rectangle bar = new Rectangle(x, y, width, 28);
            bar.setArcWidth(8);
            bar.setArcHeight(8);
            bar.setFill(Color.STEELBLUE);

            Label name = new Label(historicalFigure.name());
            name.setTextFill(Color.WHITE);
            name.setAlignment(Pos.CENTER_LEFT);
            name.setPadding(new Insets(0, 6, 0, 6));
            name.setLayoutX(x);
            name.setLayoutY(y);
            name.setPrefHeight(28);
            name.setPrefWidth(width);
            name.setStyle("-fx-font-weight: bold;");

            String tooltipText = historicalFigure.name() + " (" + historicalFigure.birthYear() + "–" + historicalFigure.deathYear() + ")";
            javafx.scene.control.Tooltip.install(bar, new javafx.scene.control.Tooltip(tooltipText));
            javafx.scene.control.Tooltip.install(name, new javafx.scene.control.Tooltip(tooltipText));

            canvas.getChildren().addAll(bar, name);
            i++;
        }
    }


    private static void plotOtherReigns(List<Monarch> monarchs, Pane canvas) {
        int i = 0;
        addGroupLabel(3, "Other Reigns", canvas);
        addGroupLine(5, canvas);

        monarchs.sort(Comparator.comparing(Monarch::startYear)
                .thenComparing(Monarch::endYear)
                .thenComparing(Monarch::name));

        for (Monarch monarch : monarchs) {
            int row = (i % 3) + 3;

            double x = LABEL_WIDTH + (monarch.startYear() - MIN_YEAR) * YEAR_WIDTH;
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
    }

    private static void plotHistoricalEvents(List<HistoricalEvent> historicalEvents, Pane canvas) {
        int i = 0;
        addGroupLabel(12, "Historical Events", canvas);
        addGroupLine(12, canvas);

        historicalEvents.sort(Comparator.comparing(HistoricalEvent::year)
                .thenComparing(HistoricalEvent::event));

        for (HistoricalEvent historicalEvent : historicalEvents) {
            double x = LABEL_WIDTH + (historicalEvent.year() - MIN_YEAR) * YEAR_WIDTH;
            double width = Math.max(20, (historicalEvent.year() - historicalEvent.year()) * YEAR_WIDTH);
            double y = 55 + 12 * ROW_HEIGHT + 8;

            Rectangle bar = new Rectangle(x, y, width, 28);
            bar.setArcWidth(8);
            bar.setArcHeight(8);
            bar.setFill(Color.STEELBLUE);

            Label name = new Label(historicalEvent.event());
            name.setTextFill(Color.WHITE);
            name.setAlignment(Pos.CENTER_LEFT);
            name.setPadding(new Insets(0, 6, 0, 6));
            name.setLayoutX(x);
            name.setLayoutY(y);
            name.setPrefHeight(28);
            name.setPrefWidth(width);
            name.setStyle("-fx-font-weight: bold;");

            String tooltipText = historicalEvent.event() + " (" + historicalEvent.year() + "–" + historicalEvent.year() + ")";
            javafx.scene.control.Tooltip.install(bar, new javafx.scene.control.Tooltip(tooltipText));
            javafx.scene.control.Tooltip.install(name, new javafx.scene.control.Tooltip(tooltipText));

            canvas.getChildren().addAll(bar, name);
            i++;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
