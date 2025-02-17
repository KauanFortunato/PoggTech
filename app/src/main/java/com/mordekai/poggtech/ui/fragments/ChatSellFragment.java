package com.mordekai.poggtech.ui.fragments;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.ChatAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Chat;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.MessageApi;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.MessageManager;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class ChatSellFragment extends Fragment {
    private SharedPrefHelper sharedPrefHelper;
    private User user;
    private ChatAdapter chatAdapter;
    private RecyclerView rvChats;
    private MessageManager messageManager;
    private MessageApi messageApi;
    private List<Chat> chatList;
    private ProgressBar progressBar;
    private TextView textNoChats;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_sell, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        initComponents(view);

        // Recycler View
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList, user.getUserId(), chat -> {
            Bundle bundle = new Bundle();
            bundle.putInt("chat_with_id", chat.getChat_with());
            bundle.putInt("chat_id", chat.getChat_id());
            bundle.putString("chat_with_name", chat.getChat_with_name());
            bundle.putString("chat_with_last_name", chat.getChat_with_last_name());
            bundle.putInt("product_id", chat.getProduct_id());
            bundle.putString("product_title", chat.getProduct_title());
            bundle.putString("product_price", String.valueOf(chat.getProduct_price()));
            bundle.putString("image_product", chat.getImage_product());

            ChatDetailFragment chatDetailFragment = new ChatDetailFragment();
            chatDetailFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerFrame, chatDetailFragment) // Usa o container correto da MainActivity
                    .addToBackStack(null)
                    .commit();
        });
        rvChats.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvChats.setNestedScrollingEnabled(false);
        rvChats.setAdapter(chatAdapter);

        // Iniciar API e gerenciador de chats
        messageApi = RetrofitClient.getRetrofitInstance().create(MessageApi.class);
        messageManager = new MessageManager(messageApi);

        swipeRefreshLayout.setOnRefreshListener(this::fetchUserChats);

        progressBar.setVisibility(View.VISIBLE);
        fetchUserChats();

        return view;
    }

    private void fetchUserChats() {
        messageManager.fetchUserChatsSell(user.getUserId(), new RepositoryCallback<List<Chat>>() {
            @Override
            public void onSuccess(List<Chat> chats) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                chatList.clear();

                if (chats.isEmpty()) {
                    rvChats.setVisibility(View.GONE);
                    textNoChats.setVisibility(View.VISIBLE);
                } else {
                    chatList.addAll(chats);
                    chatAdapter.notifyDataSetChanged();
                    textNoChats.setVisibility(View.GONE);
                    rvChats.setVisibility(View.VISIBLE);
                    Log.d("API_RESPONSE", "Item 0: " + chats.get(0).getLast_message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar chats", t);
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                rvChats.setVisibility(View.GONE);
            }
        });
    }

    private void initComponents(View view) {
        progressBar = view.findViewById(R.id.progressBarItems);
        rvChats = view.findViewById(R.id.rvChats);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        textNoChats = view.findViewById(R.id.textNoChats);
    }
}
