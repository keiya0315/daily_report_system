package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.StudentView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import constants.PropertyConst;
import services.StudentService;


public class StudentAction extends ActionBase{
	private StudentService service;

    /**
     * メソッドを実行する
     */
    @Override
    public void process() throws ServletException, IOException {

        service = new StudentService();

        //メソッドを実行
        invoke();

        service.close();
    }

    /**
     * 一覧画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void index() throws ServletException, IOException {

        //指定されたページ数の一覧画面に表示するデータを取得
        int page = getPage();
        List<StudentView> students = service.getPerPage(page);

        //全ての学生データの件数を取得
        long studentCount = service.countAll();

        putRequestScope(AttributeConst.STUDENTS, students); //取得した学生データ
        putRequestScope(AttributeConst.STU_COUNT, studentCount); //全ての学生データの件数
        putRequestScope(AttributeConst.PAGE, page); //ページ数
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE); //1ページに表示するレコードの数

        //セッションにフラッシュメッセージが設定されている場合はリクエストスコープに移し替え、セッションからは削除する
        String flush = getSessionScope(AttributeConst.FLUSH);
        if (flush != null) {
            putRequestScope(AttributeConst.FLUSH, flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        //一覧画面を表示
        forward(ForwardConst.FW_STU_INDEX);

    }
    
    /**
     * 新規登録画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void entryNew() throws ServletException, IOException {

        putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
        putRequestScope(AttributeConst.STUDENT, new StudentView()); //空の従業員インスタンス

        //新規登録画面を表示
        forward(ForwardConst.FW_STU_NEW);
    }
    
    /**
     * 新規登録を行う
     * @throws ServletException
     * @throws IOException
     */
    public void create() throws ServletException, IOException {

        //CSRF対策 tokenのチェック
        if (checkToken()) {

            //パラメータの値を元に従業員情報のインスタンスを作成する
            StudentView sv = new StudentView(
                    null,
                    getRequestParam(AttributeConst.STU_CODE),
                    getRequestParam(AttributeConst.STU_NAME),
                    getRequestParam(AttributeConst.STU_PASS),
                    toNumber(getRequestParam(AttributeConst.STU_ADMIN_FLG)),
                    null,
                    null,
                    AttributeConst.DEL_FLAG_FALSE.getIntegerValue());

            //アプリケーションスコープからpepper文字列を取得
            String pepper = getContextScope(PropertyConst.PEPPER);

            //従業員情報登録
            List<String> errors = service.create(sv, pepper);

            if (errors.size() > 0) {
                //登録中にエラーがあった場合

                putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
                putRequestScope(AttributeConst.STUDENT, sv); //入力された従業員情報
                putRequestScope(AttributeConst.ERR, errors); //エラーのリスト

                //新規登録画面を再表示
                forward(ForwardConst.FW_STU_NEW);

            } else {
                //登録中にエラーがなかった場合

                //セッションに登録完了のフラッシュメッセージを設定
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());

                //一覧画面にリダイレクト
                redirect(ForwardConst.ACT_STU, ForwardConst.CMD_INDEX);
            }

        }
    }
    
    /**
     * 詳細画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void show() throws ServletException, IOException {

        //idを条件に学生データを取得する
        StudentView sv = service.findOne(toNumber(getRequestParam(AttributeConst.STU_ID)));

        if (sv == null || sv.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {

            //データが取得できなかった、または論理削除されている場合はエラー画面を表示
            forward(ForwardConst.FW_ERR_UNKNOWN);
            return;
        }

        putRequestScope(AttributeConst.STUDENT, sv); //取得した学生情報

        //詳細画面を表示
        forward(ForwardConst.FW_STU_SHOW);
    }
    
    /**
     * 編集画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void edit() throws ServletException, IOException {

        //idを条件に学生データを取得する
        StudentView sv = service.findOne(toNumber(getRequestParam(AttributeConst.STU_ID)));

        if (sv == null || sv.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {

            //データが取得できなかった、または論理削除されている場合はエラー画面を表示
            forward(ForwardConst.FW_ERR_UNKNOWN);
            return;
        }

        putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
        putRequestScope(AttributeConst.STUDENT, sv); //取得した学生情報

        //編集画面を表示する
        forward(ForwardConst.FW_STU_EDIT);

    }
    
    /**
     * 更新を行う
     * @throws ServletException
     * @throws IOException
     */
    public void update() throws ServletException, IOException {

        //CSRF対策 tokenのチェック
        if (checkToken()) {
            //パラメータの値を元に従業員情報のインスタンスを作成する
            StudentView sv = new StudentView(
                    toNumber(getRequestParam(AttributeConst.STU_ID)),
                    getRequestParam(AttributeConst.STU_CODE),
                    getRequestParam(AttributeConst.STU_NAME),
                    getRequestParam(AttributeConst.STU_PASS),
                    toNumber(getRequestParam(AttributeConst.STU_ADMIN_FLG)),
                    null,
                    null,
                    AttributeConst.DEL_FLAG_FALSE.getIntegerValue());

            //アプリケーションスコープからpepper文字列を取得
            String pepper = getContextScope(PropertyConst.PEPPER);

            //従業員情報更新
            List<String> errors = service.update(sv, pepper);

            if (errors.size() > 0) {
                //更新中にエラーが発生した場合

                putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
                putRequestScope(AttributeConst.STUDENT, sv); //入力された従業員情報
                putRequestScope(AttributeConst.ERR, errors); //エラーのリスト

                //編集画面を再表示
                forward(ForwardConst.FW_STU_EDIT);
            } else {
                //更新中にエラーがなかった場合

                //セッションに更新完了のフラッシュメッセージを設定
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_UPDATED.getMessage());

                //一覧画面にリダイレクト
                redirect(ForwardConst.ACT_STU, ForwardConst.CMD_INDEX);
            }
        }
    }
    
    /**
     * 論理削除を行う
     * @throws ServletException
     * @throws IOException
     */
    public void destroy() throws ServletException, IOException {

        //CSRF対策 tokenのチェック
        if (checkToken()) {

            //idを条件に従業員データを論理削除する
            service.destroy(toNumber(getRequestParam(AttributeConst.STU_ID)));

            //セッションに削除完了のフラッシュメッセージを設定
            putSessionScope(AttributeConst.FLUSH, MessageConst.I_DELETED.getMessage());

            //一覧画面にリダイレクト
            redirect(ForwardConst.ACT_STU, ForwardConst.CMD_INDEX);
        }
    }

    
    /**
     * ログイン中の学生が管理者かどうかチェックし、管理者でなければエラー画面を表示
     * true: 管理者 false: 管理者ではない
     * @throws ServletException
     * @throws IOException
     */
    private boolean checkAdmin() throws ServletException, IOException {

        //セッションからログイン中の従業員情報を取得
    	StudentView sv = (StudentView) getSessionScope(AttributeConst.LOGIN_STU);

        //管理者でなければエラー画面を表示
        if (sv.getAdminFlag() != AttributeConst.ROLE_ADMIN.getIntegerValue()) {

            forward(ForwardConst.FW_ERR_UNKNOWN);
            return false;

        } else {

            return true;
        }

    }
}
