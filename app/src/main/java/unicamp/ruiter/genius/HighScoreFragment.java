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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import unicamp.ruiter.genius.to.HighScoreTO;

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
        new ScoreInputListener().execute();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    public class ScoreInputListener extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... voids) {
            String bytes = mActivity.listen();
            return bytes;
        }

        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
            if (o != null) {
                inputReceived(o);
            }
        }
    }

    public void inputReceived(final String bytes) {
        String[] buffer = bytes.substring(0, bytes.length() - 1).split("&");
        ArrayList<HighScoreTO> listScores = new ArrayList<>();
        for (String s : buffer) {
            String[] result = s.split("\\|");
            try {
                listScores.add(new HighScoreTO(result[0], Integer.valueOf(result[1])));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        HighScoreTO[] l = new HighScoreTO[listScores.size()];
        listScores.toArray(l);
        Arrays.sort(l, new Comparator<HighScoreTO>() {
            @Override
            public int compare(HighScoreTO h1, HighScoreTO h2) {
                return h2.getScore() - h1.getScore();
            }
        });

        for (HighScoreTO h : l) {
            mArrayAdapter.add("Nome: " + h.getName() + "\n" + "Pontuação: " + String.valueOf(h.getScore()));
        }

        mScoresList.setAdapter(mArrayAdapter);
    }
}
