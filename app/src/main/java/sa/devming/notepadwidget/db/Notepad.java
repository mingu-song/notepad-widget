package sa.devming.notepadwidget.db;

public class Notepad {
    private int colorId;
    private int widgetId;
    private String head;
    private String body;
    private int textSize;

    public Notepad() {}

    public Notepad(int colorId, int widgetId, String head, String body, int textSize) {
        this.colorId = colorId;
        this.head = head;
        this.body = body;
        this.widgetId = widgetId;
        this.textSize = textSize;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public int getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
}
