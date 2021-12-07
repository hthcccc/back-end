package com.example.demo.model;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CommentId implements Serializable {
    private static final long serialVersionUID = 8166059062096793134L;

    @Column(name = "comment_id", nullable = false, length = 10)
    private String commentId;

    @Column(name = "level", nullable = false)
    private Integer level;

    public CommentId(){}

    public CommentId(String comment_id,Integer lv){
        commentId=comment_id;
        level=lv;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, commentId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CommentId entity = (CommentId) o;
        return Objects.equals(this.level, entity.level) &&
                Objects.equals(this.commentId, entity.commentId);
    }
}