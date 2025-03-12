package com.mordekai.poggtech.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mordekai.poggtech.data.adapter.MessageAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Message;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiMessage;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.MessageManager;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatDetailsFragment extends Fragment {
    private RecyclerView rvMessages;
    private MessageAdapter messageAdapter;
    private MessageManager messageManager;
    private List<Message> messageList;
    private ApiMessage apiMessage;
    private EditText etMessage;
    private ImageButton btnSend;
    private ImageButton btnBack;

    private Timer timer;
    private boolean isScrolled = false;

    private int chatWithId, productId, chatChatId;
    private String chatWithName, chatWithLastName, productTitle, productPriceString, productImage;
    private User currentUser;

    private TextView nameUser, productName, productPrice, chatId;
    private ImageView imageProduct;
    private BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        initComponents(view);
        setupKeyboardVisibilityListener();

        if(getArguments() != null) {
            chatWithId = getArguments().getInt("chat_with_id");
            chatChatId = getArguments().getInt("chat_id");
            chatWithName = getArguments().getString("chat_with_name");
            chatWithLastName = getArguments().getString("chat_with_last_name");
            productId = getArguments().getInt("product_id");
            productTitle = getArguments().getString("product_title");
            productPriceString = getArguments().getString("product_price");
            productImage = getArguments().getString("image_product");

            nameUser.setText(chatWithName + " " + chatWithLastName);
            productName.setText(productTitle);
            productPrice.setText(productPriceString + " €");
            chatId.setText("ID: " + chatChatId);

            Glide.with(
                            imageProduct.getContext())
                    .load(productImage)
                    .into(imageProduct);
        }

        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(requireContext());
        currentUser = sharedPrefHelper.getUser();

        // Configurar RecyclerView
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, currentUser.getUserId());
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMessages.setAdapter(messageAdapter);

        apiMessage = RetrofitClient.getRetrofitInstance().create(ApiMessage.class);

        // Iniciar API e gerenciador de chats
        apiMessage = RetrofitClient.getRetrofitInstance().create(ApiMessage.class);
        messageManager = new MessageManager(apiMessage);

//        fetchMessages();
        timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                fetchMessages();
            }

        };

        timer.schedule(task, 0, 2000);

        btnSend.setOnClickListener(v -> {
            if(btnSend.isHapticFeedbackEnabled()) {
                btnSend.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            sendMessage();
        });

        return view;
    }

    private void fetchMessages() {
        messageManager.fetchMessages(productId, chatChatId, new RepositoryCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                boolean isNewMessage = messageList.size() != messages.size();

                messageList.clear();
                messageList.addAll(messages);
                messageAdapter.notifyDataSetChanged();

                if (isNewMessage) {
                    scrollToBottom();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ChatDetail", "Erro ao carregar mensagens", t);
            }
        });
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();

        if (!messageText.isEmpty()) {
            messageManager.sendMessage(currentUser.getUserId(), chatWithId, chatChatId, messageText, new RepositoryCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    etMessage.setText("");
                    Log.d("ChatDetail", "Resposta: " + result);
                    fetchMessages();
                    playSound(R.raw.send_message);
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("ChatDetail", "Erro ao enviar mensagem", t);
                }
            });
        }
    }

    private void initComponents(View view) {
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);

        imageProduct = view.findViewById(R.id.imageProduct);
        nameUser = view.findViewById(R.id.nameUser);
        productName = view.findViewById(R.id.productName);
        productPrice = view.findViewById(R.id.productPrice);
        btnBack = view.findViewById(R.id.btn_back);
        chatId = view.findViewById(R.id.chatId);
        getActivity().findViewById(R.id.headerContainer).setVisibility(View.GONE);

        rvMessages = view.findViewById(R.id.rvMessage);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        bottomNavigationView.setVisibility(View.GONE);
        btnSend.setVisibility(View.GONE);

        etMessage.requestFocus();
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etMessage.getText().toString().trim().isEmpty()) {
                    Log.d("ChatDetail", "Vazio: " + etMessage.getText().toString());
                    animateButtonDisappear(btnSend);
                } else {
                    Log.d("ChatDetail","Cheio: " + etMessage.getText().toString());
                    animateButtonAppear(btnSend);
                }
            }
        });

        btnBack.setOnClickListener(v -> {
            if (btnBack.isHapticFeedbackEnabled()) {
                btnBack.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            bottomNavigationView.setVisibility(View.VISIBLE);
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void animateButtonAppear(View button) {
        if (button.getVisibility() != View.VISIBLE) {
            button.setScaleX(0f);
            button.setScaleY(0f);
            button.setVisibility(View.VISIBLE);
            button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
    }

    private void animateButtonDisappear(View button) {
        if (button.getVisibility() == View.VISIBLE) {
            button.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .setDuration(150)
                    .setInterpolator(new DecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (button.getScaleX() == 0f) {
                                button.setVisibility(View.GONE);
                            }
                        }
                    })
                    .start();
        }
    }

    private void scrollToBottom() {
        if (messageAdapter.getItemCount() > 0) {
            rvMessages.scrollToPosition(messageAdapter.getItemCount() - 1);
        }
    }


    private void setupKeyboardVisibilityListener() {
        final View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) { // Se o teclado está visível
                if (!isScrolled) {
                    scrollToBottom();
                    isScrolled = true;
                }
            } else {
                isScrolled = false; // Resetar quando o teclado for fechado
            }
        });
    }

    private void playSound(int soundResourceId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), soundResourceId);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();
    }

}
