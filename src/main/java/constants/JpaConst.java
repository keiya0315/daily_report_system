package constants;

/**
 * DB関連の項目値を定義するインターフェース
 * ※インターフェイスに定義した変数は public static final 修飾子がついているとみなされる
 */
public interface JpaConst {

    //persistence-unit名
    String PERSISTENCE_UNIT_NAME = "time_schedule";

    //データ取得件数の最大値
    int ROW_PER_PAGE = 15; //1ページに表示するレコードの数

    //学生テーブル
    String TABLE_STU = "students"; //テーブル名
    //学生テーブルカラム
    String STU_COL_ID = "id"; //id
    String STU_COL_CODE = "code"; //番号
    String STU_COL_NAME = "name"; //氏名
    String STU_COL_PASS = "password"; //パスワード
    String STU_COL_ADMIN_FLAG = "admin_flag"; //管理者権限
    String STU_COL_CREATED_AT = "created_at"; //登録日時
    String STU_COL_UPDATED_AT = "updated_at"; //更新日時
    String STU_COL_DELETE_FLAG = "delete_flag"; //削除フラグ

    int ROLE_ADMIN = 1; //管理者権限ON(管理者)
    int ROLE_GENERAL = 0; //管理者権限OFF(一般)
    int STU_DEL_TRUE = 1; //削除フラグON(削除済み)
    int STU_DEL_FALSE = 0; //削除フラグOFF(現役)

    //Entity名
    String ENTITY_STU = "student"; //学生
   
    //JPQL内パラメータ
    String JPQL_PARM_CODE = "code"; //社員番号
    String JPQL_PARM_PASSWORD = "password"; //パスワード
    String JPQL_PARM_STUDENT = "student"; //学生

    //NamedQueryの nameとquery
    //全ての学生をidの降順に取得する
    String Q_STU_GET_ALL = ENTITY_STU + ".getAll"; //name
    String Q_STU_GET_ALL_DEF = "SELECT e FROM Student AS e ORDER BY e.id DESC"; //query
    //全ての学生の件数を取得する
    String Q_STU_COUNT = ENTITY_STU + ".count";
    String Q_STU_COUNT_DEF = "SELECT COUNT(e) FROM Student AS e";
    //番号とハッシュ化済パスワードを条件に未削除の学生を取得する
    String Q_STU_GET_BY_CODE_AND_PASS = ENTITY_STU + ".getByCodeAndPass";
    String Q_STU_GET_BY_CODE_AND_PASS_DEF = "SELECT e FROM Student AS e WHERE e.deleteFlag = 0 AND e.code = :" + JPQL_PARM_CODE + " AND e.password = :" + JPQL_PARM_PASSWORD;
    //指定した番号を保持する学生の件数を取得する
    String Q_STU_COUNT_RESISTERED_BY_CODE = ENTITY_STU + ".countRegisteredByCode";
    String Q_STU_COUNT_RESISTERED_BY_CODE_DEF = "SELECT COUNT(e) FROM Student AS e WHERE e.code = :" + JPQL_PARM_CODE;
 
}
