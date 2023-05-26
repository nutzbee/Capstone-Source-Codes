package foodrecommender.system.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import foodrecommender.system.R;
import foodrecommender.system.activities.SigninActivity;
import foodrecommender.system.activities.SignupActivity;
import foodrecommender.system.adapters.ProfileAdapter;
import foodrecommender.system.classes.Profile;

public class ProfileFragment extends Fragment {
    private View view;
    private RecyclerView profileRecyclerView;
    private MaterialCardView materialCardView;
    private Chip exerciseChip, summaryChip, preferencesChip, historyChip;
    private FragmentTransaction exerciseFragmentTransaction;
    private FrameLayout frameLayout;
    private Button loginButton, signupButton;
    private FrameLayout parentView;
    private boolean isLoggedIn;
    private LinearProgressIndicator linearProgressIndicator;
    private JsonObjectRequest jsonObjectRequest;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeViews();
        handleButtonActions();
        handleChipActions();
        showProfileInformation();
        updateStatus();
        return view;
    }

    private void initializeViews() {
        profileRecyclerView = view.findViewById(R.id.profile_recycler_view);
        materialCardView = view.findViewById(R.id.profile_picture_card);
        exerciseChip = view.findViewById(R.id.exercise_chip);
        summaryChip = view.findViewById(R.id.summary_chip);
        frameLayout = view.findViewById(R.id.fragment_container_summary);
        parentView = view.findViewById(R.id.parentProfileFrame);
        loginButton = view.findViewById(R.id.buttonLogin);
        signupButton = view.findViewById(R.id.buttonSignup);
        preferencesChip = view.findViewById(R.id.preferences_chip);
        historyChip = view.findViewById(R.id.history_chip);
        linearProgressIndicator = view.findViewById(R.id.profile_progress_indicator);
    }

    private void handleButtonActions() {
        if (isAdded()) {
            SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
            boolean isLoggedIn = sp.getBoolean("is_logged_in", false);
            loginButton.setOnClickListener(view -> {
                Intent intent = new Intent(requireActivity(), SigninActivity.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });

            signupButton.setOnClickListener(view -> {
                Intent intent = new Intent(requireActivity(), SignupActivity.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });

            if (!isLoggedIn) {
                loginButton.setVisibility(View.VISIBLE);
                signupButton.setVisibility(View.VISIBLE);
                profileRecyclerView.setVisibility(View.GONE);
            }
        }
    }

    private void handleChipActions() {
        exerciseChip.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                summaryChip.setChecked(false);
                preferencesChip.setChecked(false);
                historyChip.setChecked(false);
            }
            changeFragment();
        });

        summaryChip.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                exerciseChip.setChecked(false);
                preferencesChip.setChecked(false);
                historyChip.setChecked(false);
            }
            changeFragment();
        });

        preferencesChip.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                exerciseChip.setChecked(false);
                summaryChip.setChecked(false);
                historyChip.setChecked(false);
            }
            changeFragment();
        });

        historyChip.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                exerciseChip.setChecked(false);
                summaryChip.setChecked(false);
                preferencesChip.setChecked(false);
            }
            changeFragment();
        });
    }

    private void changeFragment() {
        linearProgressIndicator.show();

        if (profileRecyclerView.isShown() && isLoggedIn) {
            profileRecyclerView.setVisibility(View.GONE);
        }

        frameLayout.setVisibility(View.VISIBLE);
        exerciseFragmentTransaction = getChildFragmentManager().beginTransaction();
        if (exerciseChip.isChecked()) {
            exerciseFragmentTransaction.replace(R.id.fragment_container_summary, new ExerciseFragment());
            exerciseFragmentTransaction.commitNow();
        } else if (summaryChip.isChecked()) {
            exerciseFragmentTransaction.replace(R.id.fragment_container_summary, new SummaryFragment());
            exerciseFragmentTransaction.commitNow();
        } else if (preferencesChip.isChecked()) {
            exerciseFragmentTransaction.replace(R.id.fragment_container_summary, new PreferencesFragment());
            exerciseFragmentTransaction.commitNow();
        } else if (historyChip.isChecked()) {
            exerciseFragmentTransaction.replace(R.id.fragment_container_summary, new HistoryFragment());
            exerciseFragmentTransaction.commitNow();
        } else {
            if (frameLayout.isShown()) {
                frameLayout.setVisibility(View.GONE);
            }
            if (!profileRecyclerView.isShown() && isLoggedIn) {
                profileRecyclerView.setVisibility(View.VISIBLE);
            }
            linearProgressIndicator.hide();
        }
    }

    private void showProfileInformation() {
        if (isAdded()) {
            SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sp.edit();
            isLoggedIn = sp.getBoolean("is_logged_in", false);
            boolean isPreferences = sp.getBoolean("isPreferences", false);

            if (isPreferences) {
                profileRecyclerView.setVisibility(View.GONE);
                preferencesChip.setChecked(true);
                ed.putBoolean("isPreferences", false);
                ed.apply();
            }

            if (!isLoggedIn) {
                exerciseChip.setEnabled(false);
                historyChip.setEnabled(false);
                summaryChip.setEnabled(false);
                preferencesChip.setEnabled(false);
            } else {
                exerciseChip.setEnabled(true);
                historyChip.setEnabled(true);
                summaryChip.setEnabled(true);
                preferencesChip.setEnabled(true);
            }

            RecyclerView recyclerView = view.findViewById(R.id.profile_recycler_view);

            // Retrieve the necessary data from SharedPreferences
            String name = sp.getString("name", "");
            String email = sp.getString("email", "");
            int age = sp.getInt("age", 0);
            String username = sp.getString("username", "");
            String password = sp.getString("password", "");
            float weight = sp.getFloat("weight", 0.0f);
            float bmi = sp.getFloat("bmi", 0.0f);
            String status = sp.getString("status", "");

            // Create a list of data items
            ArrayList<Profile> profiles = new ArrayList<>();
            profiles.add(new Profile("Full Name", name));
            profiles.add(new Profile("Email", email));
            profiles.add(new Profile("Age", String.valueOf(age)));
            profiles.add(new Profile("Username", username));
            profiles.add(new Profile("Password", password));
            profiles.add(new Profile("Weight", String.valueOf(weight)));
            profiles.add(new Profile("Body Mass Index", String.valueOf(bmi)));
            if (status.isEmpty()) {
                profiles.add(new Profile("Status", "Loading status.."));
            } else {
                profiles.add(new Profile("Status", status));
            }

            ProfileAdapter profileAdapter = new ProfileAdapter(requireContext(), profiles);
            recyclerView.setAdapter(profileAdapter);
            profileAdapter.setOnItemClickListener((title, value, position) -> showSnackbar(title, value));
        }
    }

    private void updateStatus() {
        if (isAdded()) {
            if (isLoggedIn) {
                linearProgressIndicator.show();
                // String url = "http://192.168.0.41:5000/predict";
                String url = getString(R.string.predict_url);
                JSONObject data = new JSONObject();

                SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
                // Get input values from TextInputEditTexts
                int pregnancies = sp.getInt("pregnancies", 0);
                int glucose = sp.getInt("glucose", 0);
                int bloodPressure = sp.getInt("bloodPressure", 0);
                int skinThickness = sp.getInt("skinThickness", 0);
                int insulin = sp.getInt("insulin", 0);
                float bmi = sp.getFloat("bmi", 0.0f);
                float dpf = sp.getFloat("dpf", 0.0f);
                int age = sp.getInt("age", 0);

                try {
                    data.put("pregnancies", pregnancies);
                    data.put("glucose", glucose);
                    data.put("blood_pressure", bloodPressure);
                    data.put("skin_thickness", skinThickness);
                    data.put("insulin", insulin);
                    data.put("bmi", bmi);
                    data.put("diabetes_pedigree_function", dpf);
                    data.put("age", age);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, response -> {
                    try {
                        String message = response.getString("diabetes_result");
                        SharedPreferences.Editor ed = sp.edit();
                        ed.putString("status", message);
                        ed.apply();

                        showProfileInformation();
                        linearProgressIndicator.hide();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    error.printStackTrace();
                    updateStatus();
                });

                Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);
            }
        }
    }

    private void showSnackbar(String profileTitle, String profileValue) {
        if (profileTitle.equals("Status")) {
            Snackbar.make(parentView, profileValue, Snackbar.LENGTH_SHORT).show();
        }
    }
}