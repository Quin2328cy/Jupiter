package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			HttpSession session = request.getSession(false);

			JSONObject obj = new JSONObject();
			if (session != null) {
				String userId = session.getAttribute("user_id").toString();
				obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));	
			} else {
				response.setStatus(403);
				obj.put("status", "Invalid session");
			}

			RpcHelper.writeJsonObject(response, obj);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection connection = DBConnectionFactory.getConnection();
	  	 try {
	  		 JSONObject input = RpcHelper.readJSONObject(request);//request是client的request
	  		 String userId = input.getString("user_id");
	  		String password = input.getString("password");
	  		
	  		JSONObject obj = new JSONObject();// 调试时候使用
	  		if (connection.verifyLogin(userId, password)) {
	  			HttpSession session = request.getSession();
	  			session.setAttribute("user_id", userId);
	  			session.setMaxInactiveInterval(600); // 600是session有效期, 过期时间
	  			obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));
	  		} else {
	  			response.setStatus(401);
	  			obj.put("status", "User Doesn't Exists");
	  		}
	  		 
	  		
	  		 RpcHelper.writeJsonObject(response, obj);
	  		
	  	 } catch (Exception e) {
	  		 e.printStackTrace();
	  	 } finally {
	  		 connection.close();
	  	 }
	}

}
