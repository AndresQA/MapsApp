package com.example.mapsicesi.Conexion;

import com.example.mapsicesi.Models.Hueco;
import com.example.mapsicesi.Models.Usuario;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;


public class Actions {

    private HTTPSWebUtilDomi https;
    private Gson gson;


    public static String URL_PROYECT = "https://aplicaciones-moviles-401f9.firebaseio.com/";

    //METODO DE SUSCRIPCION AL EVENTO


    public Actions(){
        https = new HTTPSWebUtilDomi();
        gson = new Gson();
    }

    //Se pide el usuario. Si es nulo es porque no existe y se crea. Si ya existia no se crea
    public void registerUserIfNotExists(final Usuario usuario){
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        String url = URL_PROYECT + "/users/"+usuario.getNombre()+".json";
                        String response = https.GETrequest(url);
                        //SI EL USUARIO NO EXISTE, LO CREAMOS
                        if(response.equals("null")){
                            https.PUTrequest(url,gson.toJson(usuario));
                            // if(onRegisterListener!=null) onRegisterListener.onRegisterUser(false, user);
                        }else{
                            Usuario currentUser = gson.fromJson(response, Usuario.class);
                            //if(onRegisterListener!=null) onRegisterListener.onRegisterUser(true, currentUser);
                        }
                    }
                }
        ).start();
    }

    //Conseguir todos los usuarios
    public void getAllUsers() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url =  URL_PROYECT +"/users.json";
                String response = https.GETrequest(url);
                // Type type = new TypeToken<HashMap<String, User>>(){}.getType();
                // HashMap<String, User> users = gson.fromJson(response, type);
                // ArrayList<User> output = new ArrayList<>();
                // users.forEach( (key, value) -> output.add(value) );
                //if(onUserListListener!=null) onUserListListener.onGetUsers(output);
            }
        }).start();
    }


    public void createHueco(final Hueco hueco){
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        String url = URL_PROYECT + "/chats/"+hueco.getuId()+".json";
                        //https.PUTrequest(url,gson.toJson(chat));
                        String response = https.GETrequest(url);

                        if(response.equals("null")){
                            https.PUTrequest(url,gson.toJson(hueco));
                            //   if(onRegisterListener!=null) onRegisterListener.onRegisterUser(false, chat);
                        }else{
                            Hueco currentHueco = gson.fromJson(response, Hueco.class);

                            //  if(onRegisterListener!=null) onRegisterListener.onRegisterUser(true, currentUser);
                        }
                    }
                }
        ).start();
    }
}
