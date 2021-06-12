package com.websockets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class userServlet
 */
@WebServlet("/login")
public class userServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public userServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    static RequestDispatcher rd;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter printwriter=response.getWriter();
		
		HttpSession session=request.getSession(true);
		String username=request.getParameter("username");
		session.setAttribute("username", username);
		
		System.out.println(username);
		if(username!=null && username.startsWith("doctor")) {
			rd=request.getRequestDispatcher("/doctor.jsp");
			rd.forward(request, response);
		}
		else if(username!=null && username.startsWith("ambulance")) {
			rd=request.getRequestDispatcher("/ambulance.jsp");
			rd.forward(request, response);
		}
		else if(username!=null) {
			rd=request.getRequestDispatcher("/patient.jsp");
			rd.forward(request, response);
		}
	}

}
