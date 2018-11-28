package com.yunche.android.yunchevideosdk.demo;

import java.util.List;

/**
 * 创建人 ： shengxiao
 * 创建时间 ：2018/2/23
 * 类描述 ：用户信息
 * 备注 ：
 */

public class MemberInfo {
    private int userId;
    private String username;
    /**
     * id : 32
     * name : 登录测试账号
     * idCard : 330881199408065577
     * mobile : 15927646392
     * email : 123456@qq.com
     * dingDing : 15927646393
     * title : Java
     * entryDate : 1517983136000
     * gmtCreate : 1517983147000
     * gmtModify : 1519445115000
     * status : 0
     * type : 1
     * selected : null
     * department : [{"id":1,"name":"☁️云车总部"},{"id":2,"name":"技术部"}]
     * parent : [{"id":1,"name":"包功"}]
     */

    private int id;
    private String name;
    private String idCard;
    private String mobile;
    private String email;
    private String dingDing;
    private String title;
    private long entryDate;
    private long gmtCreate;
    private long gmtModify;
    private int status;
    private int type;
    private Object selected;
    private List<DepartmentBean> department;
    private List<ParentBean> parent;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDingDing() {
        return dingDing;
    }

    public void setDingDing(String dingDing) {
        this.dingDing = dingDing;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(long entryDate) {
        this.entryDate = entryDate;
    }

    public long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public long getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(long gmtModify) {
        this.gmtModify = gmtModify;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getSelected() {
        return selected;
    }

    public void setSelected(Object selected) {
        this.selected = selected;
    }

    public List<DepartmentBean> getDepartment() {
        return department;
    }

    public void setDepartment(List<DepartmentBean> department) {
        this.department = department;
    }

    public List<ParentBean> getParent() {
        return parent;
    }

    public void setParent(List<ParentBean> parent) {
        this.parent = parent;
    }

    public static class DepartmentBean {
        /**
         * id : 1
         * name : ☁️云车总部
         */

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ParentBean {
        /**
         * id : 1
         * name : 包功
         */

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
