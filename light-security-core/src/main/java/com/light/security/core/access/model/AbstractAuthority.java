package com.light.security.core.access.model;

import com.light.security.core.access.model.base.BaseEntity;
import com.light.security.core.access.model.tree.builder.TreeBuilderConstant;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @ClassName AbstractAuthority
 * @Description Authority接口的通用实现, 具体实现由具体子类完成
 * @Author ZhouJian
 * @Date 2019-11-20
 */
public abstract class AbstractAuthority extends BaseEntity implements Authority{

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private Integer parentId;
    private String type;
    private String name;
    private String code;
    private String desc;
    private boolean enabled;
    private boolean opened;

    protected AbstractAuthority(){
        if (logger.isDebugEnabled()){
            logger.debug("你为啥调用这个构造器啊？");
        }
    }

    protected AbstractAuthority(Builder builder){
        this.id = builder.id;
        this.parentId = builder.parentId;
        this.type = builder.type;
        this.name = builder.name;
        this.code = builder.code;
        this.desc = builder.desc;
        this.enabled = builder.enabled;
        this.opened = builder.open;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        Assert.notNull(type, "权限类型不能为空值, 可能引发NPE问题");
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    @Override
    public String getAuthorityType() {
        return getType();
    }

    @Override
    public Integer getAuthorityParentId() {
        return getParentId();
    }

    @Override
    public Integer getAuthorityId() {
        return getId();
    }

    @Override
    public String getAuthorityPoint() {
        return this.code;
    }


    @Override
    public void setAuthorityParentId(Integer parentId) {
        if (parentId != TreeBuilderConstant.NULL_KEY){
            throw new IllegalArgumentException("来自接口的对于权限父ID的修改, 参数传入非法, 参数值参考TreeBuilderConstant.NULL_KEY");
        }
        setParentId(parentId);
    }

    public static abstract class Builder extends BaseEntity.AbstractBaseBuilder{

        private Integer id;//必传参数
        private String code;

        private Integer parentId;
        private String type;
        private String name;
        private String desc;
        private boolean enabled;
        private boolean open;

        public Builder(Integer authorityId, String code){
            super(authorityId);
            if (authorityId == null || StringUtils.isEmpty(code)){
                throw new IllegalArgumentException("构造器不接受空值参数 --> authorityId is null or ( code is null or '' )");
            }
            this.id = authorityId;
            this.code = code;
        }

        public Builder parentId(Integer parentId){
            this.parentId = parentId;
            return this;
        }

        /**
         * 如果权限中存在type, 且后续过程中需要使用到type, 那么必须进行赋值
         * @param type
         * @return
         */
        public Builder type(String type){
            if (StringUtils.isEmpty(type)){
                throw new IllegalArgumentException("权限类型不能为空值 --> type is null or '' ");
            }
            this.type = type;
            return this;
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder desc(String desc){
            this.desc = desc;
            return this;
        }

        public Builder enabled(boolean enabled){
            this.enabled = enabled;
            return this;
        }

        public Builder open(boolean open){
            this.open = open;
            return this;
        }

    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
