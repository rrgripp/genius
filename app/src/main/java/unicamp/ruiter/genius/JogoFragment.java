package unicamp.ruiter.genius;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JogoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JogoFragment extends Fragment {

    private MainActivity mActivity;
    private ImageView mGreenButton;
    private ImageView mYellowButton;
    private ImageView mRedButton;
    private ImageView mBlueButton;
    private ImageView.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.green_block:
                    mActivity.sendBluetooothSerial(Constants.GREEN_IDENTIFIER);
                    break;
                case R.id.yellow_block:
                    mActivity.sendBluetooothSerial(Constants.YELLOW_IDENTIFIER);
                    break;
                case R.id.red_block:
                    mActivity.sendBluetooothSerial(Constants.RED_IDENTIFIER);
                    break;
                case R.id.blue_block:
                    mActivity.sendBluetooothSerial(Constants.BLUE_IDENTIFIER);
                    break;
            }
        }
    };

    public JogoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment JogoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JogoFragment newInstance() {
        JogoFragment fragment = new JogoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_jogo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mGreenButton = getActivity().findViewById(R.id.green_block);
        mGreenButton.setOnClickListener(mClickListener);

        mRedButton = getActivity().findViewById(R.id.red_block);
        mRedButton.setOnClickListener(mClickListener);

        mYellowButton = getActivity().findViewById(R.id.yellow_block);
        mYellowButton.setOnClickListener(mClickListener);

        mBlueButton = getActivity().findViewById(R.id.blue_block);
        mBlueButton.setOnClickListener(mClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mActivity = (MainActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        new JogoInputListener().execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class JogoInputListener extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... voids) {
            String bytes = mActivity.listenJogo();
            return bytes;
        }

        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
                if (o != null && o.equals("n")) {
                final View view = getLayoutInflater().inflate(R.layout.high_score_holder, null);

                new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Light_Dialog_Alert)
                        .setTitle("Perdeu =(")
                        .setMessage("Gostaria de salvar o resultado?")
                        .setView(view)
                        .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText nomeField = view.findViewById(R.id.high_score_edit_text);
                                nomeField.clearFocus();
                                mActivity.sendBluetooothSerial("G" + nomeField.getText().toString() + "\r");
                                mActivity.selectScoreView();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mActivity.selectHomeView();
                            }
                        })
                        .create().show();
            }
        }
    }
}
