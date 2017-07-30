package cn.lfy.base.model.type;

public enum StateType {
    ACTIVE(1, "有效"), INACTIVE(0, "停用"),DELETED(-1, "已删除"),;

    private Integer id;

    private String name;

    private StateType(Integer id, String name) {
        this.id=id;
        this.name=name;
    }

    public static StateType getById(Integer id) {
        if(null==id){
            return null;
        }
        for(StateType tmp: StateType.values()) {
            if(tmp.id.intValue() == id.intValue()) {
                return tmp;
            }
        }
        return null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

}
