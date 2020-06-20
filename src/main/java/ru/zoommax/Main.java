package ru.zoommax;

import org.json.JSONObject;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){

        File db = new File("dumpBase");
        if (!db.exists()){
            db.mkdir();
        }
        File settin = new File("settings");
        if (!settin.exists()){
            try {
                settin.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scanner in = new Scanner(System.in);
            System.out.println("bottoken");
            String bt = in.nextLine();
            System.out.println(bt+"\nbotname");
            String bn = in.nextLine();
            System.out.println(bt+"\nbotname\n"+bn+"\nserverpass");
            String sp = in.nextLine();
            try {
                FileWriter fw = new FileWriter("settings", false);
                fw.write("{\"chid\":\"\",\"botname\":\""+bn+"\",\"bottoken\":\""+bt+"\",\"sp\":\""+sp+"\"}");
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        ApiContextInitializer.init();
        TelegramBotsApi botapi = new TelegramBotsApi();
        try {
            botapi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();

        }

    }
}
class Bot extends TelegramLongPollingBot {
    protected Crypto crypto = new Crypto();
    protected String serverpass = setting("sp");

    @Override
    public void onUpdateReceived(Update update) {
        //String in = update.getMessage().getText();
        String admid = update.getMessage().getChatId().toString();
        String userget = update.getMessage().getText();
        if (userget.equals("/start")|userget.equals("/help")){
            admin("Данный бот поможет тебе создать аккаунт в дампере\n/newakk - команда для начала создания аккаунта", admid);
        }
        if (userget.equals("/newakk")) {
            admin("Придумай логин и пароль, напиши их через пробел.\nПример построения команды:\nсоздать ЛОГИН ПАРОЛЬ", admid);
        }
            String[] subStr;
            String delimeter = " "; // Разделитель
            subStr = userget.split(delimeter); // Разделения строки str с помощью метода split()
        if (subStr[0].equals("создать")) {
            if (subStr.length != 3) {
                admin("Проверь еще раз.\nсоздать Логин Пароль\nчерез пробел. Больше ничего не надо.", admid);
            } else {
                String uname = crypto.encodeSha(crypto.encodeSha(subStr[1])+crypto.encodeSha(subStr[2]));
                String upass = crypto.encrypt(uname, serverpass);
                File n = new File("dumpBase/"+uname);
                if (!n.exists()){
                    if (n.mkdir()){
                        admin("Чтобы скопировать текст - просто тапни по нему\n\n\nТвой логин в Дампере:\n`"+uname+"`\n\nТвой пароль в Дампере:\n`"+upass+"`", admid);
                    }else{
                        admin("Что-то пошло не так. Попробуй ещё раз", admid);
                    }
                }else{
                    admin("Такой пользователь уже есть. Попробуй с другой парой \"логин пароль\"", admid);
                }
            }
        }
        }


    public void admin(String text, String admid){
        SendMessage s = new SendMessage();
        s.setChatId(admid).setText(text).setParseMode("Markdown");
        try {
            execute(s);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
    public void sms(File f){
        SendPhoto s = new SendPhoto();
        s.setChatId(setting("chid")).setPhoto(f);
        try {
            execute(s);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
    public String setting(String key){
        try {
            JSONObject jo = new JSONObject(new String(Files.readAllBytes(Paths.get("settings"))));
            System.out.println(jo.getString(key));
            return jo.getString(key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getBotUsername() {
        return setting("botname");
    }

    @Override
    public String getBotToken() {
        return setting("bottoken");
    }

}

