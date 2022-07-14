import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientClass {
    public static void main(String args[]){

        Socket MySocket=null;
        DataInputStream in=null;
        DataOutputStream out=null;
        Scanner reader=new Scanner(System.in);
        try {
            MySocket=new Socket("127.0.0.1",8081);
            Send SendThread=new Send(MySocket);
            Receive ReceiveThread=new Receive(MySocket);
            SendThread.start();
            ReceiveThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
class Send extends Thread{
    Socket MySocket=null;
    String name=null;
    Scanner reader=new Scanner(System.in);
    DataOutputStream out=null;
    Send(Socket MySocket){
        this.MySocket=MySocket;
    }
    public void SetName(){
        try{
            out = new DataOutputStream(MySocket.getOutputStream());
            System.out.print("输入用户名:");
            String str = reader.nextLine();
            name=str;
            out.writeUTF(str);
        }catch (Exception e){

        }
    }
    public void SendMessage(){
        try{
            out = new DataOutputStream(MySocket.getOutputStream());
            //System.out.print("发送:");
            String str = reader.nextLine();
            out.writeUTF(str);
        }catch (Exception e){

        }
    }
    @Override
    public void run() {
        while(true) {
            try {
                if(name==null){
                    SetName();
                }
                else {
                    SendMessage();
                }
                sleep(1000);
            }catch (Exception e){}
        }
    }
}

class Receive extends Thread{
    Socket MySocket=null;
    DataInputStream in=null;
    Receive(Socket MySocket){
        this.MySocket=MySocket;
    }
    public boolean GetMessage(boolean flag){
        try {
            in = new DataInputStream(MySocket.getInputStream());
            String message = in.readUTF();
            System.out.println(message);
            sleep(1000);
        } catch (Exception e) {
            flag=false;
        }
        return flag;
    }

    @Override
    public void run() {
        while(true) {
            boolean flag=true;
            boolean flag_=GetMessage(flag);
            if(flag_==false){
                break;
            }
        }
    }
}
