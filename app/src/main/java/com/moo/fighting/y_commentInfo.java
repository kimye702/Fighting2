package com.moo.fighting;

public class y_commentInfo {
    private String profile; //프로필사진
    private String commentId; // 게시물 + 랜덤 99999 그외 시간 기준 방법 등등..
    private String writeId ;    //글 작성자 (유저데이터 테이블 = User.usersName)
    private String text;      //글 내용
    private String createAt;//글 작성,수정 시간

    public y_commentInfo(){ }

    public y_commentInfo(String profile, String commentId, String text, String writeId, String createAt){
        this.profile=profile;
        this.commentId=commentId;
        this.text=text;
        this.writeId=writeId;
        this.createAt=createAt;
    }


    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getWriteId() {
        return writeId;
    }

    public void setWriteId(String writeId) {
        this.writeId = writeId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

}
