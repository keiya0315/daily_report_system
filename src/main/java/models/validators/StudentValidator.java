package models.validators;

import java.util.ArrayList;
import java.util.List;

import actions.views.StudentView;
import constants.MessageConst;
import services.StudentService;

/**
 * 学生インスタンスに設定されている値のバリデーションを行うクラス
 *
 */
public class StudentValidator {

    /**
     * 学生インスタンスの各項目についてバリデーションを行う
     * @param service 呼び出し元Serviceクラスのインスタンス
     * @param ss StudentServiceのインスタンス
     * @param codeDuplicateCheckFlag 番号の重複チェックを実施するかどうか(実施する:true 実施しない:false)
     * @param passwordCheckFlag パスワードの入力チェックを実施するかどうか(実施する:true 実施しない:false)
     * @return エラーのリスト
     */
    public static List<String> validate(
            StudentService service, StudentView sv, Boolean codeDuplicateCheckFlag, Boolean passwordCheckFlag) {
        List<String> errors = new ArrayList<String>();

        //番号のチェック
        String codeError = validateCode(service, sv.getCode(), codeDuplicateCheckFlag);
        if (!codeError.equals("")) {
            errors.add(codeError);
        }

        //氏名のチェック
        String nameError = validateName(sv.getName());
        if (!nameError.equals("")) {
            errors.add(nameError);
        }

        //パスワードのチェック
        String passError = validatePassword(sv.getPassword(), passwordCheckFlag);
        if (!passError.equals("")) {
            errors.add(passError);
        }

        return errors;
    }

    /**
     * 番号の入力チェックを行い、エラーメッセージを返却
     * @param service StudentServiceのインスタンス
     * @param code 番号
     * @param codeDuplicateCheckFlag 番号の重複チェックを実施するかどうか(実施する:true 実施しない:false)
     * @return エラーメッセージ
     */
    private static String validateCode(StudentService service, String code, Boolean codeDuplicateCheckFlag) {

        //入力値がなければエラーメッセージを返却
        if (code == null || code.equals("")) {
            return MessageConst.E_NOSTU_CODE.getMessage();
        }

        if (codeDuplicateCheckFlag) {
            //番号の重複チェックを実施

            long StudentsCount = isDuplicateStudent(service, code);

            //同一社員番号が既に登録されている場合はエラーメッセージを返却
            if (StudentsCount > 0) {
                return MessageConst.E_STU_CODE_EXIST.getMessage();
            }
        }

        //エラーがない場合は空文字を返却
        return "";
    }

    /**
     * @param service StudentServiceのインスタンス
     * @param code 番号
     * @return 学生テーブルに登録されている同一社員番号のデータの件数
     */
    private static long isDuplicateStudent(StudentService service, String code) {

        long studentsCount = service.countByCode(code);
        return studentsCount;
    }

    /**
     * 氏名に入力値があるかをチェックし、入力値がなければエラーメッセージを返却
     * @param name 氏名
     * @return エラーメッセージ
     */
    private static String validateName(String name) {

        if (name == null || name.equals("")) {
            return MessageConst.E_NONAME.getMessage();
        }

        //入力値がある場合は空文字を返却
        return "";
    }

    /**
     * パスワードの入力チェックを行い、エラーメッセージを返却
     * @param password パスワード
     * @param passwordCheckFlag パスワードの入力チェックを実施するかどうか(実施する:true 実施しない:false)
     * @return エラーメッセージ
     */
    private static String validatePassword(String password, Boolean passwordCheckFlag) {

        //入力チェックを実施 かつ 入力値がなければエラーメッセージを返却
        if (passwordCheckFlag && (password == null || password.equals(""))) {
            return MessageConst.E_NOPASSWORD.getMessage();
        }

        //エラーがない場合は空文字を返却
        return "";
    }
}