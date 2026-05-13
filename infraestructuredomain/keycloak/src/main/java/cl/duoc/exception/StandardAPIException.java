package cl.duoc.exception;



public class StandardAPIException {
    private String type = "/errors/uncategorized";
    private String title;
    private String code;
    private String detail;
    private String instance = "/errors/uncategorized/bank";

    public StandardAPIException(String title, String code, String detail) {
        this.title = title;
        this.code = code;
        this.detail = detail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }
}
