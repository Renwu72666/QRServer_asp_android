using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using System.Web.Configuration;
using System.Data;
using System.Data.SqlClient;
using System.Collections;
using System.Web.Script.Services;
using System.Text;
/// <summary>
/// QRConnection 的摘要描述
/// </summary>
[WebService(Namespace = "http://tempuri.org/")]
[WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
[System.ComponentModel.ToolboxItem(false)]
[System.Web.Script.Services.ScriptService]
// 若要允許使用 ASP.NET AJAX 從指令碼呼叫此 Web 服務，請取消註解下列一行。
// [System.Web.Script.Services.ScriptService]
public class QRConnection : System.Web.Services.WebService
{

    public class ViewData
    {
        public string QRId { get; set; }
        public string Data { get; set; }
    }
    public QRConnection()
    {
    }

    [WebMethod]
    public string HelloWorld()
    {
        return "Hello World";
    }
    [WebMethod]
    public string Insert(string Data,string Account_name) //新增
    {

        try
        {
            string connStr = WebConfigurationManager.ConnectionStrings["connStr"].ConnectionString;
            SqlConnection conn = new SqlConnection(connStr);
            conn.Open();
            SqlCommand cmd = new SqlCommand();
            cmd.Connection = conn;
            cmd.CommandType = CommandType.StoredProcedure;
            cmd.CommandText = "[dbo].[QR_Number]";
            cmd.Parameters.Clear();
            cmd.Parameters.Add("@Data", SqlDbType.NVarChar, 1000).Value = Data;
            cmd.Parameters.Add("@Account_name", SqlDbType.NVarChar, 1000).Value = Account_name;

            cmd.ExecuteNonQuery();
            conn.Close();
            return "新增資料成功";
        }
        catch (Exception ex)
        {
            return ex.ToString();
        }
        
    }
    [WebMethod]
    public string Account_Insert(string Account, string Password) 
    {
       
        try
        {
            SqlConnection cn = new SqlConnection(WebConfigurationManager.ConnectionStrings["connStr"].ConnectionString);
            cn.Open();
            SqlCommand cmd = new SqlCommand(@"select [Account]  from [SetAccount] where Account= @Account ", cn);
            cmd.Parameters.AddWithValue("@Account", Account);
            SqlDataReader dr = cmd.ExecuteReader();

            if (dr.HasRows)
            {
                cn.Close();
                return "帳號存在";
            }
            else
            {
                SqlConnection cn_insert = new SqlConnection(WebConfigurationManager.ConnectionStrings["connStr"].ConnectionString);
                SqlCommand cmd_insert = new SqlCommand(
                      @"insert into [SetAccount] ([Account],[Password]) values (@Account,@Password)", cn_insert);
                cmd_insert.Parameters.AddWithValue("@Account", Account);
                cmd_insert.Parameters.AddWithValue("@Password", Password);
                cn_insert.Open();
                cmd_insert.ExecuteNonQuery();
                cn_insert.Close();
                return "新增成功";
            }
  
        }
        catch (Exception ex)
        {
            return ex.ToString();
        }

    }
    [WebMethod]
    public string Account_checkLogin(string Account, string Password) 
    {

        try
        {
            SqlConnection cn = new SqlConnection(WebConfigurationManager.ConnectionStrings["connStr"].ConnectionString);
            cn.Open();
            SqlCommand cmd = new SqlCommand(@"select *  from [SetAccount] where Account= @Account and Password=@Password ", cn);
            cmd.Parameters.AddWithValue("@Account", Account);
            cmd.Parameters.AddWithValue("@Password", Password);
            SqlDataReader dr = cmd.ExecuteReader();
            if (dr.HasRows)
            {
                cn.Close();
                return "true";
            }
            else
            {
                return "false";
            }
          
            cn.Close();

        }
        catch (Exception ex)
        {
            return ex.ToString();
        }

    }
    [WebMethod]
    public object showData(string Account_name)
    {
        string connStr = WebConfigurationManager.ConnectionStrings["connStr"].ConnectionString;
        ArrayList array = new ArrayList();
        using (SqlConnection conn = new SqlConnection(connStr))
        {
            DataSet ds = new DataSet();
            string selectStr = @"select * from QRSql where Account_name= @Account_name"; 
            SqlDataAdapter da = new SqlDataAdapter(selectStr, conn);
            da.SelectCommand.Parameters.AddWithValue("Account_name", Account_name);
            conn.Open();
            da.Fill(ds);
            if (ds.Tables[0].Rows.Count <= 0)
            {
                array.Add(-1 +"");
                array.Add(-1 +"");

            }          
            foreach (DataRow dr in ds.Tables[0].Rows)
            {
                array.Add(dr["QRId"].ToString());
                array.Add(dr["Data"].ToString());
                 
            }
        }
        return (string[])array.ToArray(typeof(string));
    }
    [WebMethod]
    public string delete(string QRId)
    {
        try
        {
            SqlConnection cn = new SqlConnection(WebConfigurationManager.ConnectionStrings["connStr"].ConnectionString);
            cn.Open();
            SqlCommand cmd = new SqlCommand(@"select *  from QRSql where QRId= @QRId ", cn);
            cmd.Parameters.AddWithValue("@QRId", QRId);
            SqlDataReader dr = cmd.ExecuteReader();
            if (dr.HasRows)
            {
                cn.Close();
                cn.Open();
                SqlCommand cmd_Del = new SqlCommand(@"delete from QRSql where QRId= @QRId ", cn);
                cmd_Del.Parameters.AddWithValue("@QRId", QRId);
                cmd_Del.ExecuteNonQuery();
                cn.Close();
                //return "true";
                return "刪除成功";
            }
            else
            {
                return "刪除失敗，資料庫尚未有該筆資料";
                cn.Close();
            }

            cn.Close();
        }
        catch (Exception ex)
        {
            return ex.ToString();
        }
    }
    [WebMethod]
    public string updata(string QRId, string Data) 
    {

        try
        {
            SqlConnection cn = new SqlConnection(
                 WebConfigurationManager.ConnectionStrings["connStr"].ConnectionString);

            SqlCommand cmd = new SqlCommand(
                @"update  QRSql set Data=@Data where QRId =@QRId ", cn);
            cmd.Parameters.AddWithValue("@QRId", QRId);
            cmd.Parameters.AddWithValue("@Data", Data);
            cn.Open();
            cmd.ExecuteNonQuery();
            cn.Close();
            return "true";     
        }
        catch (Exception ex)
        {
            return ex.ToString();
        }
    }
    /* public string findId(int QRId)//查詢單筆
     {
         ArrayList one_array = new ArrayList();
         try
         {
             SqlConnection cn = new SqlConnection(
                  WebConfigurationManager.ConnectionStrings["connStr"].ConnectionString);

             SqlCommand cmd = new SqlCommand(
                 @"select * from  QRSql where QRId= @QRId ", cn);
             DataSet ds = new DataSet();
             SqlDataAdapter da = new SqlDataAdapter(cmd, cn);
             cmd.Parameters.AddWithValue("@QRId", QRId);
             cn.Open();
             cmd.Fill(ds);
             foreach (DataRow dr in ds.Tables[0].Rows)
             {
                 one_array.Add(dr["QRId"].ToString());
                 one_array.Add(dr["Data"].ToString());
             }
             cmd.ExecuteNonQuery();
             cn.Close();
         }
         catch (Exception ex)
         {
             return ex.ToString();
         }
         return (string[])one_array.ToArray(typeof(string));
     }*/
    [WebMethod]
    public  string[] find_one(string Data)
    {
        string connStr = WebConfigurationManager.ConnectionStrings["connStr"].ConnectionString;

        ArrayList array = new ArrayList();

        using (SqlConnection conn = new SqlConnection(connStr))
        {
            DataSet ds = new DataSet();
            string selectStr = @"SELECT * FROM  QRSql Where Data Like '" + Data + "%' ";
            SqlDataAdapter da = new SqlDataAdapter(selectStr, conn);
            conn.Open();
            da.Fill(ds);
            foreach (DataRow dr in ds.Tables[0].Rows)
            {
                array.Add(dr["QRId"].ToString());
                array.Add(dr["Data"].ToString());
            }

        }
        return (string[])array.ToArray(typeof(string));

    }
    private string findd = "";
    [WebMethod]
    [ScriptMethod(ResponseFormat = ResponseFormat.Json)]
    public string GetUserInfoString(string QRId, string Data)
    {
        return QRId + "," + Data;
    }
    [WebMethod]
    [ScriptMethod(ResponseFormat = ResponseFormat.Json)]
    public ViewData GetOneUserInfo(string QRId, string Data)
    {
        return (new ViewData { QRId = QRId, Data = Data });

    }

    [WebMethod]
    [ScriptMethod(ResponseFormat = ResponseFormat.Json)]
    public ViewData[] showData2()
    {
        List<ViewData> res = new List<ViewData>();
        string connStr = WebConfigurationManager.ConnectionStrings["connStr"].ConnectionString;

        ArrayList array = new ArrayList();
        using (SqlConnection conn = new SqlConnection(connStr))
        {
            DataSet ds = new DataSet();
            string selectStr = @"select* from QRSql ";
            SqlDataAdapter da = new SqlDataAdapter(selectStr, conn);
            conn.Open();
            da.Fill(ds);
            foreach (DataRow dr in ds.Tables[0].Rows)
            {
                res.Add(new ViewData { QRId = dr["QRId"].ToString() , Data = dr["Data"].ToString() });
                  string msg= dr["QRId"].ToString();
            }
        }
   
        return res.ToArray();
    }
    public static SqlConnection sqlCon;
    private String ConServerStr = "Data Source=RENWU-PC;Initial Catalog = QRServer; Persist Security Info=True;User ID = sa; Password=abcd1234";
    private string msg = "";
    [WebMethod]
    public string show()
    {
        try
        {
            sqlCon = new SqlConnection();
            sqlCon.ConnectionString = ConServerStr;
            sqlCon.Open();
            string sql = "select * from QRSql";
            SqlCommand cmd = new SqlCommand(sql, sqlCon);
            SqlDataReader reader = cmd.ExecuteReader();
            while (reader.Read())
            {
                msg = msg + reader[0]  + "  " + reader[1] + "\n";  // + "   " + reader[3] + "  " + reader[4] 
            }
            reader.Close();
            cmd.Dispose();
        }
        catch (Exception)
        {

        }
        return msg;
    }
 

}
