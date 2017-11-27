package unicamp.ruiter.genius;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Ruiter on 26/11/2017.
 */

public class HighScoreFragment extends Fragment implements MainActivity.OnReceiveInputStream{

    private MainActivity mActivity;
    private ArrayAdapter<String> mArrayAdapter;
    private ListView mScoresList;


    public HighScoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HighScoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HighScoreFragment newInstance() {
        HighScoreFragment fragment = new HighScoreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        new ScoreInputListener().doInBackground(null);
//        Log.d(Constants.TAG, "Entered Score listener thread");
//        String buffer = mActivity.listen();
//        parseInput(buffer);
//        Log.d(Constants.TAG, "Score listener thread receiverd\n" + buffer);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_high_score, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        mScoresList = view.findViewById(R.id.scores_list);

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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class ScoreInputListener extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            mActivity.listen();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mScoresList.setAdapter(mArrayAdapter);
        }


    }

    public void inputReceived(final String bytes) {
        String[] buffer = bytes.split("&");
        for (String s : buffer) {
            String[] result = s.split("\\|");
            mArrayAdapter.add(result[0] + "\n" + result[1]);
        }
    }
}
