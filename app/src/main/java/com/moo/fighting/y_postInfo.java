package com.moo.fighting;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class y_postInfo implements Parcelable {
    private String profile=""; //프로필사진
    private String postId=""; // 게시물 + 랜덤 99999 그외 시간 기준 방법 등등..
    private String writeId="" ;    //글 작성자 (유저데이터 테이블 = User.usersName)
    private String contentTitle="";      //글 제목
    private String text="";      //글 내용
    private String createAt="";//글 작성,수정 시간

    private int commentnum=0;
    private ArrayList<String> likeList;            //좋아요 수
    private ArrayList<String> imgList;// 이미지 url 링크들

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Fighting2");
    private FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

    private int heartnum=0;
    private Map<String, Boolean> hearts = new HashMap<>();

    public y_postInfo(Parcel parcel) {
        // must be same order with writeToParcel()
        this.profile = parcel.readString();
        this.postId = parcel.readString();
        this.writeId = parcel.readString();
        this.contentTitle = parcel.readString();
        this.text = parcel.readString();
        this.createAt = parcel.readString();
        this.commentnum = parcel.readInt();
        this.heartnum = parcel.readInt();
        this.imgList=parcel.readArrayList(String.class.getClassLoader());
        this.likeList=parcel.readArrayList(String.class.getClassLoader());
    }

    public y_postInfo(){ }

    public y_postInfo(String contentTitle, String text, String writeId){
        this.contentTitle=contentTitle;
        this.text=text;
        this.writeId=writeId;
    }

    public y_postInfo(String profile, String contentTitle, String text, String writeId, String createAt, int heartnum,
                      int commentnum, String postId){
        this.profile=profile;
        this.contentTitle=contentTitle;
        this.text=text;
        this.writeId=writeId;
        this.createAt=createAt;
        this.heartnum=heartnum;
        this.commentnum=commentnum;
        this.postId=postId;
    }


    public static final Parcelable.Creator<y_postInfo> CREATOR = new Parcelable.Creator<y_postInfo>() {
        @Override
        public y_postInfo createFromParcel(Parcel parcel) {
            return new y_postInfo(parcel);
        }
        @Override
        public y_postInfo[] newArray(int size) {
            return new y_postInfo[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.profile);
        dest.writeString(this.postId);
        dest.writeString(this.writeId);
        dest.writeString(this.contentTitle);
        dest.writeString(this.text);
        dest.writeString(this.createAt);
        dest.writeInt(this.commentnum);
        dest.writeInt(this.heartnum);
        dest.writeList(this.imgList);
        dest.writeList(this.likeList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getWriteId() {
        return writeId;
    }

    public void setWriteId(String writeId) {
        this.writeId = writeId;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
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

    public ArrayList<String> getLikeList() {
        return likeList;
    }

    public void setLikeList(ArrayList<String> likeList) {
        this.likeList = likeList;
    }

    public ArrayList<String> getImgList() {
        return imgList;
    }

    public void setImgList(ArrayList<String> imgList) {
        this.imgList = imgList;
    }

    public int getHeartnum() {
        return heartnum;
    }

    public void setHeartnum(int heartnum) {
        this.heartnum = heartnum;
    }
    public int getCommentnum() {
        return commentnum;
    }

    public void setCommentnum(int commentnum) {
        this.commentnum = commentnum;
    }

    public Map<String, Boolean> getHearts() {
        return hearts;
    }

    public void setHearts(Map<String, Boolean> hearts) {
        this.hearts = hearts;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("heartnum", heartnum);
        result.put("commentnum", commentnum);
        return result;
    }
}
