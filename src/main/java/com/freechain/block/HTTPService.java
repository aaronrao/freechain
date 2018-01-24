package com.freechain.block;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.alibaba.fastjson.JSON;

/**
 * @author aaron.rao
 *
 */
public class HTTPService {
    private BlockChain chain;

    public HTTPService(BlockChain blockChain) {
        this.chain = blockChain;
    }

    public void initHTTPServer(int port) {
        try {
            Server server = new Server(port);
            System.out.println("listening http port on: " + port);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);
            //挖矿
            context.addServlet(new ServletHolder(new MineServlet()), "/mine");
            //交易
            context.addServlet(new ServletHolder(new NewTransactionServlet()), "/transactions/new");
            //查询区块链
            context.addServlet(new ServletHolder(new ChainServlet()), "/chain");
            //注册节点
            context.addServlet(new ServletHolder(new RegisterPeerServlet()), "/peers/register");
            //替换共识长链
            context.addServlet(new ServletHolder(new ResolvePeerServlet()), "/peers/resolve");
            server.start();
            server.join();
        } catch (Exception e) {
            System.out.println("init http server is error:" + e.getMessage());
        }
    }

    private class MineServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        	resp.getWriter().print(chain.mine());
        }
    }
    
    private class NewTransactionServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            Transaction trx = JSON.parseObject(getReqBody(req), Transaction.class);
            int blockId = chain.createTransaction(trx.getSender(), trx.getRecipient(), trx.getAmount());
            resp.getWriter().print("Your transaction will be included in block " + blockId);
        }
    }

    private class ChainServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        	resp.getWriter().print(chain.getFullChain());
        }
    }

    private class RegisterPeerServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().print(chain.registerPeers(req.getParameter("urls").split(",")));
        }
    }
    
    private class ResolvePeerServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        	resp.getWriter().print(chain.consensus());
        }
    }
    
    private String getReqBody(HttpServletRequest req) throws IOException {
		BufferedReader br = req.getReader();
    	String str, body = "";
    	while((str = br.readLine()) != null){
    		body += str;
    	}
		return body;
	}
}

