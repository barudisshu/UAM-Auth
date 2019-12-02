package com.cplier.platform.entity;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
@SuppressWarnings("serial")
public abstract class BaseEntity implements Serializable, Cloneable {
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }

    /**
     * Avoid call-by-reference, use deep-copy. alternatively, use a pojo object to convert.
     * @return this
     * @throws CloneNotSupportedException e
     */
//    @Override
    protected Object clone() throws CloneNotSupportedException {
        return SerializationUtils.clone(this);
    }
}
