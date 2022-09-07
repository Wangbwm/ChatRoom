import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerClass {
    private static CopyOnWriteArrayList<ServerThread> all = new CopyOnWriteArrayList<ServerThread>();//保存用户

    public static void main(String args[]) throws IOException {

        ServerSocket server = null;//创建嵌套字
        Socket OnServer = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        Scanner reader = new Scanner(System.in);
        System.out.println("Waiting");
        server = new ServerSocket(8081);
        try {//等待连接
            while (true) {
                OnServer = server.accept();
                ServerThread a = new ServerThread(OnServer);//每接收到一个连接就分出一个线程
                all.add(a);//容器添加线程信息
                new Thread(a).start();//运行子线程

            }

        } catch (Exception e) {
        }
    }

    static class ServerThread extends Thread {//服务子线程，继承Thread
        Socket OnServer = null;

        String name = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        Scanner reader = new Scanner(System.in);

        ServerThread(Socket OnServer) {
            this.OnServer = OnServer;
        }

        public void SetName(){//设置用户名字
            try {
                in = new DataInputStream(OnServer.getInputStream());
                out = new DataOutputStream(OnServer.getOutputStream());
                String str = in.readUTF();
                name = str;
                System.out.println("客户端地址:" + OnServer.getInetAddress() + ": " + "用户名:" + str);
                this.out.writeUTF("欢迎进入聊天室！\n当前在线人数：" + all.size());
                for (ServerThread other : all) {//在all中遍历，除了自己都发送进入信息
                    if (other == this) { //自己
                        continue;
                    } else {
                        other.out.flush();
                        str = "进入聊天室";
                        other.out.writeUTF(name + str + '\n' + "当前在线人数：" + all.size());

                    }
                }
            }catch (Exception e){

            }
        }

        public boolean Send(boolean flag) {//转发函数
            try {
                in = new DataInputStream(OnServer.getInputStream());
                out = new DataOutputStream(OnServer.getOutputStream());
                String str = in.readUTF();
                String[] temp = str.split("@");//判断要发送的信息是不是私聊信息
                boolean IsPrivate = false;//用于判断私聊信息与否
                String PrivateName=new String();
                StringBuffer SendStr=new StringBuffer();//发送缓冲区
                int index=0;
                for (int i = 0; i < temp.length; ++i) {
                    for (ServerThread other : all) {
                        if (temp[i].compareTo(other.name)==0) {
                            IsPrivate = true;//判断为私聊
                            PrivateName=other.name;
                            index=i;
                        }
                    }
                }
                for (int i = 0; i < temp.length; ++i) {
                    if(i!=index){
                        SendStr.append(temp[i]);
                    }
                }
                String Send=SendStr.toString();
                if (IsPrivate == true) {
                    PrivateChat(PrivateName,Send);
                }else{
                    System.out.println("客户端地址:" + OnServer.getInetAddress() + ": " + name + ":" + str);
                    for (ServerThread other : all) {
                        if (other == this) { //自己
                            continue;
                        } else {
                            other.out.flush();
                            other.out.writeUTF(name + ":" + str);
                        }
                    }
                }
                sleep(1000);
            } catch (Exception e) {
                try {
                    out = new DataOutputStream(OnServer.getOutputStream());
                    for (ServerThread other : all) {
                        if (other == this) { //自己
                            continue;
                        } else {
                            other.out.flush();
                            String str = "退出聊天室";
                            other.out.writeUTF(name + str);

                        }
                    }
                    for (ServerThread other_ : all) {
                        if (other_ == this) { //自己
                            all.remove(other_);
                            System.out.println(name + "已退出");
                            out.close();
                        } else {

                        }
                    }
                    for (ServerThread other_ : all) {//退出后更新发送当前在线人数
                        other_.out.writeUTF("当前在线人数：" + all.size());
                    }


                } catch (Exception m) {
                    flag=false;
                }
            }
            return flag;
        }
        public void PrivateChat(String PrivateName,String str) {//私聊函数
            try {
                System.out.println("客户端地址:" + OnServer.getInetAddress() + ": " + name +" 私聊"+PrivateName+":" + str);
                for (ServerThread other : all) {
                    if (other.name.compareTo(PrivateName) == 0) {
                        other.out.flush();
                        other.out.writeUTF(name+" @你" + ":" + str);
                    }
                }
            }catch (Exception e){}
        }
        @Override
        public void run() {
            while (true) {

                if (name == null) {
                    SetName();
                } else {
                    boolean flag=true;
                    boolean flag_=Send(flag);
                    if(flag_==false){
                        break;
                    }

                }

            }
        }

    }
}
