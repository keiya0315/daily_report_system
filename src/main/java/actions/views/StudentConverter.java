package actions.views;

import java.util.ArrayList;
import java.util.List;

import constants.AttributeConst;
import constants.JpaConst;
import models.Student;

public class StudentConverter {
	 /**
     * ViewモデルのインスタンスからDTOモデルのインスタンスを作成する
     * @param sv StudentViewのインスタンス
     * @return Studentのインスタンス
     */
    public static Student toModel(StudentView sv) {

        return new Student(
                sv.getId(),
                sv.getCode(),
                sv.getName(),
                sv.getPassword(),
                sv.getAdminFlag() == null
                        ? null
                        : sv.getAdminFlag() == AttributeConst.ROLE_ADMIN.getIntegerValue()
                                ? JpaConst.ROLE_ADMIN
                                : JpaConst.ROLE_GENERAL,
                sv.getCreatedAt(),
                sv.getUpdatedAt(),
                sv.getDeleteFlag() == null
                        ? null
                        : sv.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()
                                ? JpaConst.STU_DEL_TRUE
                                : JpaConst.STU_DEL_FALSE);
    }

    /**
     * DTOモデルのインスタンスからViewモデルのインスタンスを作成する
     * @param s Studentのインスタンス
     * @return StudentViewのインスタンス
     */
    public static StudentView toView(Student s) {

        if(s == null) {
            return null;
        }

        return new StudentView(
                s.getId(),
                s.getCode(),
                s.getName(),
                s.getPassword(),
                s.getAdminFlag() == null
                        ? null
                        : s.getAdminFlag() == JpaConst.ROLE_ADMIN
                                ? AttributeConst.ROLE_ADMIN.getIntegerValue()
                                : AttributeConst.ROLE_GENERAL.getIntegerValue(),
                s.getCreatedAt(),
                s.getUpdatedAt(),
                s.getDeleteFlag() == null
                        ? null
                        : s.getDeleteFlag() == JpaConst.STU_DEL_TRUE
                                ? AttributeConst.DEL_FLAG_TRUE.getIntegerValue()
                                : AttributeConst.DEL_FLAG_FALSE.getIntegerValue());
    }

    /**
     * DTOモデルのリストからViewモデルのリストを作成する
     * @param list DTOモデルのリスト
     * @return Viewモデルのリスト
     */
    public static List<StudentView> toViewList(List<Student> list) {
        List<StudentView> svs = new ArrayList<>();

        for (Student s : list) {
            svs.add(toView(s));
        }

        return svs;
    }

    /**
     * Viewモデルの全フィールドの内容をDTOモデルのフィールドにコピーする
     * @param s DTOモデル(コピー先)
     * @param sv Viewモデル(コピー元)
     */
    public static void copyViewToModel(Student s, StudentView sv) {
        s.setId(sv.getId());
        s.setCode(sv.getCode());
        s.setName(sv.getName());
        s.setPassword(sv.getPassword());
        s.setAdminFlag(sv.getAdminFlag());
        s.setCreatedAt(sv.getCreatedAt());
        s.setUpdatedAt(sv.getUpdatedAt());
        s.setDeleteFlag(sv.getDeleteFlag());

    }
}
