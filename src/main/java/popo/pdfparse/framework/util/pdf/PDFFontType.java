package popo.pdfparse.framework.util.pdf;

public enum PDFFontType {

    ARIAL_MT("ArialMT");

    private String fontName;

    PDFFontType(String fontName) {
        this.fontName = fontName;
    }
    
    public String getFontTypeName() {
        return fontName;
    }
}
