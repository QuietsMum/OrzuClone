package orzu.org;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import orzu.org.chat.ChatAdapter;
import orzu.org.chat.chatItems;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatsView.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatsView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsView extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView rv_chats;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ChatAdapter adapter_chats;
    private OnFragmentInteractionListener mListener;
    Context context;
    private int status;
    Dialog dialog;
    List<chatItems> logos;
    String mMessage;
    Bitmap btmp;
    String idUser;



    public ChatsView() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatsView.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatsView newInstance(String param1, String param2) {
        ChatsView fragment = new ChatsView();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_chats_view, container, false);
        final SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(" ", Context.MODE_PRIVATE);
        idUser = prefs.getString(Util.TASK_USERID, "");
        rv_chats = view.findViewById(R.id.rv_chats);
        rv_chats.setHasFixedSize(true);
        rv_chats.setNestedScrollingEnabled(false);
        rv_chats.setItemViewCacheSize(20);
        rv_chats.setDrawingCacheEnabled(true);
        rv_chats.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv_chats.setLayoutManager(llm);

        try {
            getChats();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void getChats() throws IOException {
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=user_chats&act=view_chats&user_id=" + idUser ;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                requireActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    getChats();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        });
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.show();
                            }
                        }, 500);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                mMessage = Objects.requireNonNull(response.body()).string();
                String user_first;
                String user_second;
                try {

                    JSONArray jsonArray = new JSONArray(mMessage);
                    int lenght = jsonArray.length();
                    logos = new ArrayList<>(lenght);
                    for (int i = 0; i < lenght; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String chat_id = jsonObject.getString("chat_id");
                        user_first = jsonObject.getString("user_first");
                        user_second = jsonObject.getString("user_second");
                        String user_first_username = jsonObject.getString("user_first_username");
                        String user_first_avatar = jsonObject.getString("user_first_avatar");
                        String user_second_username = jsonObject.getString("user_second_username");
                        String user_second_avatar = jsonObject.getString("user_second_avatar");
                        String last_message = jsonObject.getString("last_message");
                        String last_message_time = jsonObject.getString("last_message_time");
                        String mes_delivered = jsonObject.getString("mes_delivered");
                        String mes_saw = jsonObject.getString("mes_saw");
                        if (user_first.equals(Common.userId)){
                                logos.add(i, new chatItems( user_second_username,user_second, chat_id, mes_delivered, mes_saw, last_message, last_message_time, user_second_avatar) );
                        } else  logos.add(i, new chatItems( user_first_username, user_first, chat_id, mes_delivered, mes_saw, last_message, last_message_time, user_first_avatar) );

                    }
                    adapter_chats = new ChatAdapter(getContext(), logos);
                    adapter_chats.setClickListener(new ChatAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(getContext(), ChatActivity.class);
                            Common.chatId =  logos.get(position).getchat_id();
                            Common.user_to = logos.get(position).getId();
                            Common.nameOfChat = logos.get(position).getName();
                            startActivity(intent);
                        }
                    });
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rv_chats.setAdapter(adapter_chats);
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
