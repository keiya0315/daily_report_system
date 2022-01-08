package services;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.NoResultException;

import actions.views.StudentConverter;
import actions.views.StudentView;
import constants.JpaConst;
import models.Student;
import models.validators.StudentValidator;
import utils.EncryptUtil;

/**
 * 学生テーブルの操作に関わる処理を行うクラス
 */
public class StudentService extends ServiceBase{
	/**
     * 指定されたページ数の一覧画面に表示するデータを取得し、EmployeeViewのリストで返却する
     * @param page ページ数
     * @return 表示するデータのリスト
     */
    public List<StudentView> getPerPage(int page) {
        List<Student> students = em.createNamedQuery(JpaConst.Q_STU_GET_ALL, Student.class)
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();

        return StudentConverter.toViewList(students);
    }

    /**
     * 学生テーブルのデータの件数を取得し、返却する
     * @return 学生テーブルのデータの件数
     */
    public long countAll() {
        long empCount = (long) em.createNamedQuery(JpaConst.Q_STU_COUNT, Long.class)
                .getSingleResult();

        return empCount;
    }

    /**
     * 番号、パスワードを条件に取得したデータをStudentViewのインスタンスで返却する
     * @param code 番号
     * @param plainPass パスワード文字列
     * @param pepper pepper文字列
     * @return 取得データのインスタンス 取得できない場合null
     */
    public StudentView findOne(String code, String plainPass, String pepper) {
        Student s = null;
        try {
            //パスワードのハッシュ化
            String pass = EncryptUtil.getPasswordEncrypt(plainPass, pepper);

            //社員番号とハッシュ化済パスワードを条件に未削除の従業員を1件取得する
            s = em.createNamedQuery(JpaConst.Q_STU_GET_BY_CODE_AND_PASS, Student.class)
                    .setParameter(JpaConst.JPQL_PARM_CODE, code)
                    .setParameter(JpaConst.JPQL_PARM_PASSWORD, pass)
                    .getSingleResult();

        } catch (NoResultException ex) {
        }

        return StudentConverter.toView(s);

    }

    /**
     * idを条件に取得したデータをStudentViewのインスタンスで返却する
     * @param id
     * @return 取得データのインスタンス
     */
    public StudentView findOne(int id) {
        Student s = findOneInternal(id);
        return StudentConverter.toView(s);
    }

    /**
     * 番号を条件に該当するデータの件数を取得し、返却する
     * @param code 番号
     * @return 該当するデータの件数
     */
    public long countByCode(String code) {

        //指定した社員番号を保持する従業員の件数を取得する
        long students_count = (long) em.createNamedQuery(JpaConst.Q_STU_COUNT_RESISTERED_BY_CODE, Long.class)
                .setParameter(JpaConst.JPQL_PARM_CODE, code)
                .getSingleResult();
        return students_count;
    }

    /**
     * 画面から入力された学生の登録内容を元にデータを1件作成し、学生テーブルに登録する
     * @param sv 画面から入力された学生の登録内容
     * @param pepper pepper文字列
     * @return バリデーションや登録処理中に発生したエラーのリスト
     */
    public List<String> create(StudentView sv, String pepper) {

        //パスワードをハッシュ化して設定
        String pass = EncryptUtil.getPasswordEncrypt(sv.getPassword(), pepper);
        sv.setPassword(pass);

        //登録日時、更新日時は現在時刻を設定する
        LocalDateTime now = LocalDateTime.now();
        sv.setCreatedAt(now);
        sv.setUpdatedAt(now);

        //登録内容のバリデーションを行う
        List<String> errors = StudentValidator.validate(this, sv, true, true);

        //バリデーションエラーがなければデータを登録する
        if (errors.size() == 0) {
            create(sv);
        }

        //エラーを返却（エラーがなければ0件の空リスト）
        return errors;
    }

    /**
     * 画面から入力された従業員の更新内容を元にデータを1件作成し、従業員テーブルを更新する
     * @param ev 画面から入力された従業員の登録内容
     * @param pepper pepper文字列
     * @return バリデーションや更新処理中に発生したエラーのリスト
     */
    public List<String> update(StudentView sv, String pepper) {

        //idを条件に登録済みの従業員情報を取得する
        StudentView savedStu = findOne(sv.getId());

        boolean validateCode = false;
        if (!savedStu.getCode().equals(sv.getCode())) {
            //番号を更新する場合

            //番号についてのバリデーションを行う
            validateCode = true;
            //変更後の番号を設定する
            savedStu.setCode(sv.getCode());
        }

        boolean validatePass = false;
        if (sv.getPassword() != null && !sv.getPassword().equals("")) {
            //パスワードに入力がある場合

            //パスワードについてのバリデーションを行う
            validatePass = true;

            //変更後のパスワードをハッシュ化し設定する
            savedStu.setPassword(
                    EncryptUtil.getPasswordEncrypt(sv.getPassword(), pepper));
        }

        savedStu.setName(sv.getName()); //変更後の氏名を設定する
        savedStu.setAdminFlag(sv.getAdminFlag()); //変更後の管理者フラグを設定する

        //更新日時に現在時刻を設定する
        LocalDateTime today = LocalDateTime.now();
        savedStu.setUpdatedAt(today);

        //更新内容についてバリデーションを行う
        List<String> errors = StudentValidator.validate(this, savedStu, validateCode, validatePass);

        //バリデーションエラーがなければデータを更新する
        if (errors.size() == 0) {
            update(savedStu);
        }

        //エラーを返却（エラーがなければ0件の空リスト）
        return errors;
    }

    /**
     * idを条件に従業員データを論理削除する
     * @param id
     */
    public void destroy(Integer id) {

        //idを条件に登録済みの従業員情報を取得する
        StudentView savedStu = findOne(id);

        //更新日時に現在時刻を設定する
        LocalDateTime today = LocalDateTime.now();
        savedStu.setUpdatedAt(today);

        //論理削除フラグをたてる
        savedStu.setDeleteFlag(JpaConst.STU_DEL_TRUE);

        //更新処理を行う
        update(savedStu);

    }

    /**
     * 社員番号とパスワードを条件に検索し、データが取得できるかどうかで認証結果を返却する
     * @param code 社員番号
     * @param plainPass パスワード
     * @param pepper pepper文字列
     * @return 認証結果を返却す(成功:true 失敗:false)
     */
    public Boolean validateLogin(String code, String plainPass, String pepper) {

        boolean isValidEmployee = false;
        if (code != null && !code.equals("") && plainPass != null && !plainPass.equals("")) {
            StudentView sv = findOne(code, plainPass, pepper);

            if (sv != null && sv.getId() != null) {

                //データが取得できた場合、認証成功
                isValidEmployee = true;
            }
        }

        //認証結果を返却する
        return isValidEmployee;
    }

    /**
     * idを条件にデータを1件取得し、Employeeのインスタンスで返却する
     * @param id
     * @return 取得データのインスタンス
     */
    private Student findOneInternal(int id) {
        Student s = em.find(Student.class, id);

        return s;
    }

    /**
     * 学生データを1件登録する
     * @param sv 学生データ
     * @return 登録結果(成功:true 失敗:false)
     */
    private void create(StudentView sv) {

        em.getTransaction().begin();
        em.persist(StudentConverter.toModel(sv));
        em.getTransaction().commit();

    }

    /**
     * 従業員データを更新する
     * @param ev 画面から入力された従業員の登録内容
     */
    private void update(StudentView sv) {

        em.getTransaction().begin();
        Student s = findOneInternal(sv.getId());
        StudentConverter.copyViewToModel(s, sv);
        em.getTransaction().commit();

    }
}
