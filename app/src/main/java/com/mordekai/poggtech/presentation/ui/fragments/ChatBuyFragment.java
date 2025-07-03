package com.mordekai.poggtech.presentation.ui.fragments;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.ChatAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Chat;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiMessage;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.MessageManager;
import com.mordekai.poggtech.utils.ChatSearchable;
import com.mordekai.poggtech.utils.MessageNotifier;
import com.mordekai.poggtech.utils.NotificationFlagHelper;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import com.mordekai.poggtech.utils.Utils;

import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class ChatBuyFragment extends Fragment implements ChatSearchable {
    private SharedPrefHelper sharedPrefHelper;
    private User user;
    private ChatAdapter chatAdapter;
    private RecyclerView rvChats;
    private MessageManager messageManager;
    private ApiMessage apiMessage;
    private List<Chat> chatList;
    private TextView textNoChats;
    private List<Chat> originalChatList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_buy, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        initComponents(view);

        // Recycler View
        chatList = new ArrayList<>();

        chatAdapter = new ChatAdapter(chatList, user.getUserId(), chat -> {
            Utils.goToChat(this, chat);
        });

        rvChats.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvChats.setNestedScrollingEnabled(false);
        rvChats.setAdapter(chatAdapter);

        // Iniciar API e gerenciador de chats
        apiMessage = RetrofitClient.getRetrofitInstance().create(ApiMessage.class);
        messageManager = new MessageManager(apiMessage);

        fetchUserChats();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (NotificationFlagHelper.hasNewMessage(requireContext())) {
            fetchUserChats();
            NotificationFlagHelper.clearNewMessageFlag(requireContext());
        }

        MessageNotifier.setListener(() -> fetchUserChats());

        fetchUserChats();
    }

    @Override
    public void onPause() {
        super.onPause();
        MessageNotifier.clearListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchUserChats();
    }

    private void fetchUserChats() {
        messageManager.getUserChatsBuy(user.getUserId(), new RepositoryCallback<List<Chat>>() {
            @Override
            public void onSuccess(List<Chat> chats) {
                swipeRefreshLayout.setRefreshing(false);

                originalChatList.clear();
                originalChatList.addAll(chats);

                chatList.clear();

                if (chats.isEmpty()) {
                    textNoChats.setVisibility(View.VISIBLE);
                    rvChats.setVisibility(View.GONE);
                } else {
                    chatList.addAll(chats);
                    chatAdapter.notifyDataSetChanged();
                    Log.d("API_RESPONSE", "Item 0: " + chats.get(0).getLastMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                swipeRefreshLayout.setRefreshing(false);

                Log.e("API_RESPONSE", "Erro ao buscar chats", t);

                textNoChats.setVisibility(View.VISIBLE);

                rvChats.setVisibility(View.GONE);
            }
        });
    }

    private void initComponents(View view) {
        rvChats = view.findViewById(R.id.rvChats);
        textNoChats = view.findViewById(R.id.textNoChats);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> fetchUserChats());
    }

    @Override
    public void onSearchQueryChanged(String query) {
        chatList.clear();

        if (query == null || query.trim().isEmpty()) {
            chatList.addAll(originalChatList); // mostrar tudo se pesquisa estiver vazia
        } else {
            String lowerQuery = query.toLowerCase();
            for (Chat chat : originalChatList) {

                // Filtra nome do utilizador, nome do produto e ultima mensagem
                if ((chat.getChatWithName() != null && chat.getChatWithName().toLowerCase().contains(lowerQuery)) ||
                                (chat.getLastMessage() != null && chat.getLastMessage().toLowerCase().contains(lowerQuery)) ||
                                (chat.getProductTitle() != null && chat.getProductTitle().toLowerCase().contains(lowerQuery))
                ) {
                    chatList.add(chat);
                }
            }
        }

        chatAdapter.notifyDataSetChanged();
    }

}
