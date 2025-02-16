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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class ChatBuyFragment extends Fragment {
    private SharedPrefHelper sharedPrefHelper;
    private User user;
    private ChatAdapter chatAdapter;
    private RecyclerView rvChats;
    private MessageManager messageManager;
    private MessageApi messageApi;
    private List<Chat> chatList;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_buy, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        initComponents(view);

        // Recycler View
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList, user.getUserId());
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
        messageManager.fetchUserChats(user.getUserId(), new RepositoryCallback<List<Chat>>() {
            @Override
            public void onSuccess(List<Chat> chats) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                chatList.clear();

                if (chats.isEmpty()) {
                    rvChats.setVisibility(View.GONE);
                } else {
                    chatList.addAll(chats);
                    chatAdapter.notifyDataSetChanged();

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
    }
}
