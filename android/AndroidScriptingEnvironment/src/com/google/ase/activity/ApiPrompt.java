/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.ase.activity;

import com.google.ase.AseAnalytics;
import com.google.ase.Constants;
import com.google.ase.R;
import com.google.ase.facade.FacadeConfiguration;
import com.google.ase.rpc.MethodDescriptor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Prompts for API parameters.
 *
 * <p>This activity is started by {@link ApiBrowser} to prompt user for RPC
 * call parameters. Input/output interface is RPC name and explicit parameter
 * values.
 * 
 * @author igor.v.karp@gmail.com (Igor Karp)
 */
public class ApiPrompt extends Activity implements OnClickListener {
  private MethodDescriptor mRpc;
  private String[] mHints;
  private String[] mValues;
  private ApiPromptAdapter mAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    if (preferences.getBoolean("editor_fullscreen", true)) {
      CustomizeWindow.requestFullscreen(this);
    } else {
      CustomizeWindow.requestNoTitle(this);
    }
    setContentView(R.layout.api_prompt);
    mRpc = FacadeConfiguration.getMethodDescriptor(
        getIntent().getStringExtra(Constants.EXTRA_API_PROMPT_RPC_NAME));
    mHints = mRpc.getParameterHints();
    mValues = getIntent().getStringArrayExtra(Constants.EXTRA_API_PROMPT_VALUES);
    mAdapter = new ApiPromptAdapter();
    ((ListView) findViewById(R.id.list)).setAdapter(mAdapter);
    ((Button) findViewById(R.id.done)).setOnClickListener(this);
    AseAnalytics.trackActivity(this);
    setResult(RESULT_CANCELED);
  }

  @Override
  public void onClick(View v) {
    Intent intent = new Intent();
    intent.putExtra(Constants.EXTRA_API_PROMPT_RPC_NAME, mRpc.getName());
    intent.putExtra(Constants.EXTRA_API_PROMPT_VALUES, mValues);
    setResult(RESULT_OK, intent);
    finish();
  }

  private class ApiPromptAdapter extends BaseAdapter {
    @Override
    public int getCount() {
      return mHints.length;
    }

    @Override
    public Object getItem(int position) {
      return mValues[position];
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      EditText edit = new EditText(ApiPrompt.this);
      edit.setText(mValues[position]);
      edit.setHint(mHints[position]);
      edit.addTextChangedListener(new ValueWatcher(position));
      return edit;
    }
  }

  private class ValueWatcher implements TextWatcher {
    private final int mPosition;

    public ValueWatcher(int position) {
      mPosition = position;
    }

    @Override
    public void afterTextChanged(Editable e) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      mValues[mPosition] = s.toString();
    }
  }
}
