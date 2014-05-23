/*
 * ******************************************************************************
 *   Copyright (c) 2013-2014 Gabriele Mariotti.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  *****************************************************************************
 */

package it.gmariotti.cardslib.demo.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import it.gmariotti.cardslib.demo.R;
import it.gmariotti.cardslib.demo.db.CardCursorContract;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridCursorAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.view.CardGridView;

/**
 * List with Cursor Example
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class GridCursorCardFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    MyCursorCardAdapter mAdapter;
    CardGridView mGridView;

    @Override
    public int getTitleResourceId() {
        return R.string.carddemo_title_grid_cursor;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.demo_fragment_grid_cursor, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }


    private void init() {

        mAdapter = new MyCursorCardAdapter(getActivity());

        mGridView = (CardGridView) getActivity().findViewById(R.id.carddemo_grid_cursor);
        if (mGridView != null) {
            mGridView.setAdapter(mAdapter);
        }

        // Force start background query to load sessions
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader = null;
        loader = new CursorLoader(getActivity(), CardCursorContract.CardCursor.CONTENT_URI,
                CardCursorContract.CardCursor.ALL_PROJECTION, null , null, CardCursorContract.CardCursor.DEFAULT_SORT);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (getActivity() == null) {
            return;
        }
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    public class MyCursorCardAdapter extends CardGridCursorAdapter {

        public MyCursorCardAdapter(Context context) {
            super(context);
        }

        @Override
        protected Card getCardFromCursor(Cursor cursor) {
            MyCursorCard card = new MyCursorCard(super.getContext());
            setCardFromCursor(card,cursor);

            //Create a CardHeader
            CardHeader header = new CardHeader(getActivity(),R.layout.carddemo_inner_gridcursor_header);
            //Set the header title

            header.setTitle(card.mainHeader);
            header.setPopupMenu(R.menu.popupmain, new CardHeader.OnClickCardHeaderPopupMenuListener() {
                @Override
                public void onMenuItemClick(BaseCard card, MenuItem item) {
                    Toast.makeText(getContext(), "Click on card="+card.getId()+" item=" +  item.getTitle(), Toast.LENGTH_SHORT).show();
                }
            });

            //Add Header to card
            card.addCardHeader(header);



            CardThumbnail thumb = new CardThumbnail(getActivity());
            thumb.setDrawableResource(card.resourceIdThumb);
            card.addCardThumbnail(thumb);

            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Toast.makeText(getContext(), "Card id=" + card.getId() + " Title=" + card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
                }
            });


            return card;
        }

        private void setCardFromCursor(MyCursorCard card,Cursor cursor) {

            card.mainTitle=cursor.getString(CardCursorContract.CardCursor.IndexColumns.TITLE_COLUMN);
            card.secondaryTitle=cursor.getString(CardCursorContract.CardCursor.IndexColumns.SUBTITLE_COLUMN);
            card.mainHeader=cursor.getString(CardCursorContract.CardCursor.IndexColumns.HEADER_COLUMN);
            card.setId(""+cursor.getInt(CardCursorContract.CardCursor.IndexColumns.ID_COLUMN));

            int thumb = cursor.getInt(CardCursorContract.CardCursor.IndexColumns.THUMBNAIL_COLUMN);
            switch (thumb){
                case 0:
                    card.resourceIdThumb=R.drawable.ic_ic_launcher_web;
                    break;
                case 1:
                    card.resourceIdThumb=R.drawable.ic_ic_dh_net;
                    break;
                case 2:
                    card.resourceIdThumb=R.drawable.ic_tris;
                    break;
                case 3:
                    card.resourceIdThumb=R.drawable.ic_info;
                    break;
                case 4:
                    card.resourceIdThumb=R.drawable.ic_smile;
                    break;
            }

        }
    }

    private void removeCard(Card card) {

        //Use this code to delete items on DB
        ContentResolver resolver = getActivity().getContentResolver();
        long noDeleted = resolver.delete
                (CardCursorContract.CardCursor.CONTENT_URI,
                        CardCursorContract.CardCursor.KeyColumns.KEY_ID + " = ? ",
        new String[]{card.getId()});

        //mAdapter.notifyDataSetChanged();

    }

    public class MyCursorCard extends Card {

        String mainTitle;
        String secondaryTitle;
        String mainHeader;
        int resourceIdThumb;

        public MyCursorCard(Context context) {
            super(context, R.layout.carddemo_cursor_inner_content);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            //Retrieve elements
            TextView mTitleTextView = (TextView) parent.findViewById(R.id.carddemo_cursor_main_inner_title);
            TextView mSecondaryTitleTextView = (TextView) parent.findViewById(R.id.carddemo_cursor_main_inner_subtitle);

            if (mTitleTextView != null)
                mTitleTextView.setText(mainTitle);

            if (mSecondaryTitleTextView != null)
                mSecondaryTitleTextView.setText(secondaryTitle);

        }
    }
}
