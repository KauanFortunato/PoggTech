package com.mordekai.poggtech.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import com.mordekai.poggtech.data.remote.MessageApi;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.MessageManager;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;

public class ChatDetailFragment extends Fragment {
    private RecyclerView rvMessages;
    private MessageAdapter messageAdapter;
    private MessageManager messageManager;
    private List<Message> messageList;
    private MessageApi messageApi;
    private EditText etMessage;
    private ImageButton btnSend;
    private ImageButton btnBack;

    private int chatWithId, productId;
    private String chatWithName, chatWithLastName, productTitle, productPriceString, productImage;
    private User currentUser;

    private TextView nameUser, productName, productPrice;
    private ImageView imageProduct;
    private BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        initComponents(view);

        if(getArguments() != null) {
            chatWithId = getArguments().getInt("chat_with_id");
            chatWithName = getArguments().getString("chat_with_name");
            chatWithLastName = getArguments().getString("chat_with_last_name");
            productId = getArguments().getInt("product_id");
            productTitle = getArguments().getString("product_title");
            productPriceString = getArguments().getString("product_price");
            productImage = getArguments().getString("image_product");


            nameUser.setText(chatWithName + " " + chatWithLastName);
            productName.setText(productTitle);
            productPrice.setText(productPriceString + " €");

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

        messageApi = RetrofitClient.getRetrofitInstance().create(MessageApi.class);

        // Iniciar API e gerenciador de chats
        messageApi = RetrofitClient.getRetrofitInstance().create(MessageApi.class);
        messageManager = new MessageManager(messageApi);

        fetchMessages();

        btnSend.setOnClickListener(v -> {
            if(btnSend.isHapticFeedbackEnabled()) {
                btnSend.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            sendMessage();
        });

        return view;
    }

    private void fetchMessages() {
        messageManager.fetchMessages(currentUser.getUserId(), chatWithId, productId, new RepositoryCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                messageList.clear();
                messageList.addAll(messages);
                messageAdapter.notifyDataSetChanged();

                // Rolar automaticamente para a última mensagem
                if (!messageList.isEmpty()) {
                    rvMessages.scrollToPosition(messageList.size() - 1);
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
            messageManager.sendMessage(currentUser.getUserId(), chatWithId, productId, messageText, new RepositoryCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    etMessage.setText("");
                    Log.d("ChatDetail", "Resposta: " + result);
                    fetchMessages();
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

        // Inicializar componentes
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

}
