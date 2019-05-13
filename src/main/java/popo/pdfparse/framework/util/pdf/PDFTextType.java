package popo.pdfparse.framework.util.pdf;

public enum PDFTextType {

    BOLD("FILL_STROKE"),
    NON_BOLD("FILL");

    private String textType;

    PDFTextType(String textType) {
        this.textType = textType;
    }

    public String getTextType() {
        return textType;
    }
}

