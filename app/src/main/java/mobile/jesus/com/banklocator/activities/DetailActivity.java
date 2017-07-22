package mobile.jesus.com.banklocator.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import mobile.jesus.com.banklocator.R;
import mobile.jesus.com.banklocator.databinding.DetailActivityBinding;

/**
 * Created by jr02815 on 7/21/2017.
 */

public class DetailActivity extends Activity {
    DetailActivityBinding binding;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.detail_activity);
        Intent intent = getIntent();
        binding.address.setText(intent.getStringExtra("address"));
        binding.rating.setText("Is Open: "+ intent.getDoubleExtra("rating", 0.0));
        binding.isOpen.setText("Rating : "+ intent.getBooleanExtra("isOpen", false));
        binding.name.setText(intent.getStringExtra("name"));
    }
}
