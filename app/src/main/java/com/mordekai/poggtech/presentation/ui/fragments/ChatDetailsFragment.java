package com.mordekai.poggtech.presentation.ui.fragments;

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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.MessageAdapter;
import com.mordekai.poggtech.data.model.Message;
import com.mordekai.poggtech.data.model.MessageDateSeparator;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiMessage;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.MessageManager;
import com.mordekai.poggtech.presentation.ui.activity.MainActivity;
import com.mordekai.poggtech.utils.MessageNotifier;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import com.mordekai.poggtech.utils.Utils;
import com.mordekai.poggtech.presentation.viewmodel.MessageViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatDetailsFragment extends Fragment {
    private RecyclerView rvMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList = new ArrayList<>();

    private EditText etMessage;
    private ImageButton btnSend, btnBack;
    private TextView nameUser, productName, productPrice, chatId;
    private ImageView imageProduct;

    private int chatWithId, productId, chatChatId;
    private String chatWithName, productTitle, productPriceString, productImage;
    private User currentUser;
    private boolean isScrolled = false;


    private MessageViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(requireContext());
        currentUser = sharedPrefHelper.getUser();

        initComponents(view);
        setupKeyboardVisibilityListener();

        // ViewModel
        ApiMessage apiMessage = RetrofitClient.getRetrofitInstance().create(ApiMessage.class);
        MessageManager messageManager = new MessageManager(apiMessage);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MessageViewModel(messageManager);
            }
        }).get(MessageViewModel.class);

        if (getArguments() != null) {
            chatWithId = getArguments().getInt("chat_with_id");
            chatChatId = getArguments().getInt("chat_id");
            chatWithName = getArguments().getString("chat_with_name");
            productId = getArguments().getInt("product_id");
            productTitle = getArguments().getString("product_title");
            productPriceString = getArguments().getString("product_price");
            productImage = getArguments().getString("image_product");

            nameUser.setText(chatWithName);
            productName.setText(productTitle);
            productPrice.setText(productPriceString + " €");
            chatId.setText("ID: " + chatChatId);

            Utils.loadImageBasicAuth(imageProduct, productImage);

            viewModel.markMessagesAsRead(chatChatId, currentUser.getUserId());
        }

        messageAdapter = new MessageAdapter(messageList, currentUser.getUserId());
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMessages.setAdapter(messageAdapter);

        setupObservers();

        viewModel.loadMessages(productId, chatChatId);
        MessageNotifier.setListener(() -> viewModel.loadMessages(productId, chatChatId));

        btnSend.setOnClickListener(v -> {
            if (btnSend.isHapticFeedbackEnabled()) {
                btnSend.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }
            sendMessage();
        });

        return view;
    }

    private void setupObservers() {
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            boolean isNewMessage = messageList.size() != messages.size();
            messageList.clear();
            messageList.addAll(insertDateSeparators(messages));
            messageAdapter.notifyDataSetChanged();
            if (isNewMessage) {
                scrollToBottom();
            }
        });

        viewModel.getSendStatus().observe(getViewLifecycleOwner(), msg -> {
            etMessage.setText("");
            playSound(R.raw.send_message);
            viewModel.loadMessages(productId, chatChatId);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), err -> {
            Log.e("ChatDetail", "Erro: " + err);
        });
    }

    private void initComponents(View view) {
        imageProduct = view.findViewById(R.id.imageProduct);
        nameUser = view.findViewById(R.id.nameUser);
        productName = view.findViewById(R.id.productName);
        productPrice = view.findViewById(R.id.productPrice);
        btnBack = view.findViewById(R.id.btn_back);
        chatId = view.findViewById(R.id.chatId);

        rvMessages = view.findViewById(R.id.rvMessage);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        btnSend.setVisibility(View.GONE);

        etMessage.requestFocus();
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (etMessage.getText().toString().trim().isEmpty()) {
                    animateButtonDisappear(btnSend);
                } else {
                    animateButtonAppear(btnSend);
                }
            }
        });

        btnBack.setOnClickListener(v -> {
            if (btnBack.isHapticFeedbackEnabled()) {
                btnBack.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
            navController.popBackStack();
        });
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // Cria a mensagem localmente
            Message pendingMessage = new Message();
            pendingMessage.setSender_id(currentUser.getUserId());
            pendingMessage.setReceiver_id(chatWithId);
            pendingMessage.setMessage(messageText);
            pendingMessage.setPending(true);

            messageList.add(pendingMessage);
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            scrollToBottom();
            etMessage.setText("");
            playSound(R.raw.send_message);

            // Envia ao servidor (sem esperar resposta para atualizar a UI)
            viewModel.sendMessage(currentUser.getUserId(), chatWithId, chatChatId, messageText);
        }
    }

    private List<Message> insertDateSeparators(List<Message> originalMessages) {
        List<Message> result = new ArrayList<>();
        String lastDateLabel = "";

        for (Message msg : originalMessages) {
            if (msg instanceof MessageDateSeparator) continue; // proteção extra

            String currentLabel = msg.getDate_label(); // ← você precisa garantir que o campo exista no model
            if (currentLabel == null) currentLabel = "Desconhecido";

            if (!currentLabel.equals(lastDateLabel)) {
                result.add(new MessageDateSeparator(currentLabel));
                lastDateLabel = currentLabel;
            }

            result.add(msg);
        }

        return result;
    }


    private void animateButtonAppear(View button) {
        if (button.getVisibility() != View.VISIBLE) {
            button.setScaleX(0f);
            button.setScaleY(0f);
            button.setVisibility(View.VISIBLE);
            button.animate().scaleX(1f).scaleY(1f).setDuration(150).setInterpolator(new DecelerateInterpolator()).start();
        }
    }

    private void animateButtonDisappear(View button) {
        if (button.getVisibility() == View.VISIBLE) {
            button.animate()
                    .scaleX(0f).scaleY(0f)
                    .setDuration(150).setInterpolator(new DecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override public void onAnimationEnd(Animator animation) {
                            if (button.getScaleX() == 0f) button.setVisibility(View.GONE);
                        }
                    }).start();
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
            if (keypadHeight > screenHeight * 0.15) {
                if (!isScrolled) {
                    scrollToBottom();
                    isScrolled = true;
                }
            } else {
                isScrolled = false;
            }
        });
    }

    private void playSound(int soundResourceId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), soundResourceId);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();
    }
}
