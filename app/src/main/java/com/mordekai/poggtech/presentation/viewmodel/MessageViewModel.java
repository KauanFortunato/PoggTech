package com.mordekai.poggtech.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Chat;
import com.mordekai.poggtech.data.model.Message;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.domain.MessageManager;

import java.util.List;

public class MessageViewModel extends ViewModel {
    private final MessageManager messageManager;

    // LiveData para observação na UI
    private final MutableLiveData<List<Message>> messages = new MutableLiveData<>();
    private final MutableLiveData<List<Chat>> chatsBuy = new MutableLiveData<>();
    private final MutableLiveData<List<Chat>> chatsSell = new MutableLiveData<>();
    private final MutableLiveData<Chat> currentChat = new MutableLiveData<>();
    private final MutableLiveData<Integer> unreadCount = new MutableLiveData<>();
    private final MutableLiveData<String> sendStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> createdChatId = new MutableLiveData<>();
    private final MutableLiveData<Boolean> markedAsRead = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public MessageViewModel(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    public LiveData<List<Chat>> getChatsBuy() {
        return chatsBuy;
    }

    public LiveData<List<Chat>> getChatsSell() {
        return chatsSell;
    }

    public LiveData<Chat> getCurrentChat() {
        return currentChat;
    }

    public LiveData<Integer> getUnreadCount() {
        return unreadCount;
    }

    public LiveData<String> getSendStatus() {
        return sendStatus;
    }

    public LiveData<Integer> getCreatedChatId() {
        return createdChatId;
    }

    public LiveData<Boolean> getMarkedAsRead() {
        return markedAsRead;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }


    // Métodos públicos para chamar no Fragment/Activity

    public void sendMessage(int senderId, int receiverId, int chatId, String msg) {
        messageManager.sendMessage(senderId, receiverId, chatId, msg, new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String result) {
                sendStatus.postValue(result);
            }

            @Override
            public void onFailure(Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void loadMessages(int productId, int chatId) {
        messageManager.fetchMessages(productId, chatId, new RepositoryCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> result) {
                messages.postValue(result);
            }

            @Override
            public void onFailure(Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void loadUserChatsBuy(int userId) {
        messageManager.fetchUserChatsBuy(userId, new RepositoryCallback<List<Chat>>() {
            @Override
            public void onSuccess(List<Chat> result) {
                chatsBuy.postValue(result);
            }

            @Override
            public void onFailure(Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void loadUserChatsSell(int userId) {
        messageManager.fetchUserChatsSell(userId, new RepositoryCallback<List<Chat>>() {
            @Override
            public void onSuccess(List<Chat> result) {
                chatsSell.postValue(result);
            }

            @Override
            public void onFailure(Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void loadChat(int userId, int productId) {
        messageManager.fetchChat(userId, productId, new RepositoryCallback<Chat>() {
            @Override
            public void onSuccess(Chat result) {
                currentChat.postValue(result);
            }

            @Override
            public void onFailure(Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void loadUnreadCount(int chatId, int receiverId) {
        messageManager.fetchUnread(chatId, receiverId, new RepositoryCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                unreadCount.postValue(result);
            }

            @Override
            public void onFailure(Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void markMessagesAsRead(int chatId, int receiverId) {
        messageManager.markIsRead(chatId, receiverId, new RepositoryCallback<ApiResponse<Void>>() {
            @Override
            public void onSuccess(ApiResponse<Void> result) {
                // Opcional: você pode atualizar algo no estado se quiser
            }

            @Override
            public void onFailure(Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void createChat(int productId) {
        messageManager.createChat(productId, new RepositoryCallback<Integer>() {
            @Override
            public void onSuccess(Integer chatId) {
                createdChatId.postValue(chatId);
            }

            @Override
            public void onFailure(Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void fetchUnread(int chatId, int receiverId) {
        messageManager.fetchUnread(chatId, receiverId, new RepositoryCallback<Integer>() {
            @Override
            public void onSuccess(Integer count) {
                unreadCount.postValue(count);
            }

            @Override
            public void onFailure(Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

}
