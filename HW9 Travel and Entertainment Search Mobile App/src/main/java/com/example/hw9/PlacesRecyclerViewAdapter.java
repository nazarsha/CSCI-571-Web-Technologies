package com.example.hw9;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
        import android.support.v4.app.FragmentManager;
        import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
        import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.location.places.Place;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.hw9.MainActivity.TAG;

public class PlacesRecyclerViewAdapter extends
        RecyclerView.Adapter<PlacesRecyclerViewAdapter.ViewHolder> {

    private List<Place> placesList;
    private Context context;
    private boolean isInFavTab;
    private  ProgressDialog progressDiag;


//    private String curDet = "[{\"id\": \"ChIJHZPurvnAwoARr1cE7CnTOgA\",         \"name\": \"Sista Mary's Soul Food\",         \"address\": \"420 W Colorado St, Glendale, CA 91204, USA\",         \"lat\": 34.1423376,         \"lon\": -118.2619206,         \"phone\": \"+1 818-396-5399\",         \"zip\": \"91204\",         \"website\": \"http://www.sistamaryssoulfood.com/\",         \"rating\": 4.4,         \"url\": \"https://maps.google.com/?cid=16557725656045487\",         \"google_reviews\": [             {                 \"auth_name\": \"Imelda Murillo\",                 \"pic\": \"https://lh3.googleusercontent.com/-ZmG49SOYmMM/AAAAAAAAAAI/AAAAAAAAAAA/AGi4gfxcyZA2pdUx8L6l6wdZtg3UtDwC9Q/s128-c0x00000000-cc-rp-mo/photo.jpg\",                 \"rating\": 5,                 \"text\": \"All the food here is delicious. Wonderful place. Great service. I just can't wait to go back and eat some more food from here especially the grilled lemon chicken is very delicious.  My favored.\",                 \"time\": 1519588507             },             {                 \"auth_name\": \"Joe Cinocca\",                 \"pic\": \"https://lh6.googleusercontent.com/-wEHelS-Upa8/AAAAAAAAAAI/AAAAAAAADBc/proHWB4t3Ok/s128-c0x00000000-cc-rp-mo-ba2/photo.jpg\",                 \"rating\": 5,                 \"text\": \"I had the Crab Mac ‘n Chrese, which was divine. My friend enjoyed the Smothered Chicken w/ Candied Yams and Cornbread. Tip : Get the Purple Rain\\n\\nClassy music.\\nCozy seating.\\nNice TV action.\\n\\nVery pleasant experience and plan on bringing friends to verify the awesomeness. Marcus was the bomb dot com\",                 \"time\": 1518929339             },             {                 \"auth_name\": \"Sara Rivers\",                 \"pic\": \"https://lh6.googleusercontent.com/-NCD0d6bSHpE/AAAAAAAAAAI/AAAAAAAAAAA/AGi4gfwRDe8emFUOI2axGGx5yjuy8qr4Dg/s128-c0x00000000-cc-rp-mo/photo.jpg\",                 \"rating\": 4,                 \"text\": \"The decor was pretty nice.  I would consider this as an upscale soulfood venue, based on the presentation of the food and ingredients used to appease the population in the area.  The food was fantastic.  The best Mac&Cheese I ever had.  The fried chicken and Oxtails were amazing as well, Seasoned perfectly.   The wait staff was very patient and attentive during our experience.  My family and I are defintely going back!!\",                 \"time\": 1518892615             },             {                 \"auth_name\": \"Rachel Hall\",                 \"pic\": \"https://lh3.googleusercontent.com/-02pl7FdqIeA/AAAAAAAAAAI/AAAAAAAAAA4/WFmeJ7Ipu2U/s128-c0x00000000-cc-rp-mo/photo.jpg\",                 \"rating\": 5,                 \"text\": \"Being from the South, I am always on the lookout for some good comfort food.  I heard about this place from some coworkers, who all loved it.  I was curious, so I gave it a try.  I ordered the fried chicken, mac and cheese, and greens.  The fried chicken was some of the best I've had in LA - great flavor in the seasoning.  The mac and cheese was great - not too cheesy and good flavor.  And last but certainly not least, the greens are the best I've had outside of the South.  I would go back for the greens alone, but there's so many more menu items I really want to try.  And to top it all off, everyone who works there is super nice.\",                 \"time\": 1503878980             },             {                 \"auth_name\": \"Nneka Egbe\",                 \"pic\": \"https://lh5.googleusercontent.com/-nrxHUyAxxpI/AAAAAAAAAAI/AAAAAAAAYdQ/W6hc7WOksLU/s128-c0x00000000-cc-rp-mo/photo.jpg\",                 \"rating\": 4,                 \"text\": \"Im from the south, so when im in Cali im always on the hunt for a good soulfood spot. This place hits the mark! Best macaroni n cheese I've had...ever!\",                 \"time\": 1520554538             }         ],         \"photos\": [             \"CmRaAAAAYe3s1d-g1fJEoGBYk19qYhIJzKii8FvE38sbtzxc9cdPPkwPI0_rpNi8XzcWYwrISKWsgZSc_GHvmaq8zYOgcC38fJpv8YsEKNzvMEirWNUecsvcOZWEugwQbIutl1uxEhC-pxvzknEFQnpTexgU57FzGhRFwHJB8RHAWpyYjhB3qXoHGgr_hw\",             \"CmRaAAAAw13t1yw3ZE9jmWDMpIYuR-11fZ1UmAoSZvvAlOyed2UPGywcSLgGqEUvWL1blnn8GxCiURZ_qYTRz3AsfQZCXas19rYWoU9i-btH54TOADWUDzWCaFxH5qqqLlgcNN8rEhB9VOtfb0TZgV38qcyTA-x-GhQ-7VyNOdcyAlwze4zBE1BCT9arVw\",             \"CmRaAAAAPQq2djCtkVDxbnqzC1KEjQPSvyyf6yKS8oSwpLSCE9fson7beAhh3BYklOv_N6RY4n8aAQrf6j5j2yGCks3ekfOyooqTD66LiFy_NCgjN7EGgD9IOlMMCR_WUXjnB4GOEhBKwK6Cd3qQllgBG8IPbQkKGhSglEpN2nJdF-ZgV2M1lSSDkU_wbQ\",             \"CmRaAAAAJkn5tLJgqUAo3EzRP1pw6UfVBbDL1GcyQXLVmvntSgvqLDbV11LscruX40V1rmoP75qtI-vlq2sqO7wzLKfLDWSUrXN2lPteuOHFWfRFvjC0SUXWi_W8b4pfoudXH46MEhB9cP5zacHc3WYVvPY3YWuCGhSdRz39zQYZ-w-_S26O-CxZm6RmdA\",             \"CmRaAAAAq3YMcGA-gvtTzBITNBFVz7cmh-2Uwe3lhwoXNy8NsbtQc4VWAGN0hUXZaO54qpra5Oyr7YkKIQlwYN7ONNhu8oVrYgr0gQ2JAfQvPizQ8RmcZqorne6CuS5QOB7Syso6EhCMDvcSgtZ8HfFJgS1qjcFtGhQBFoAx15kodKribgjP0Sx_lnQexg\",             \"CmRaAAAA2iEZJQrQ5hNqrZFcztaSq7SaR14flWBd-K3iUDCOQSoGQIAJzvINETy9PzNrLY7DsA3zevv-oidN3EHCqoPMF3V9Xk1IBh0RbfL-7Xv2GME369uKn1Yg5s0reYX1thWyEhC9XCEr2u8DsQ_up1DTAhRmGhSRs-v0AWfAUpn76vUxzEeay_qbjw\",             \"CmRaAAAAxst5KqdKW7mNAYN4js36o44nYpqsmf_i-no7XUWqoVRgRxB1DdGzLU3gWBvFPQ_XPqbon3SWi71rVIzzhmhRQImtd43Ub5oQ3dZ5P1giBpvvnjUTGBgepf07QceWI5FZEhDVPyHPbqqcC5vDuXc2OOx0GhSV0bnExTTx24QxWIn7bBaXPvO6Zg\",             \"CmRaAAAAaQYWqsTdB202m5flM9jSBHdZRzEf-m2OyIryXighUFKUQ0kktkpAVUo3IODXI7N1-DkxUGlBr1DZPvLixwCL6LmvwJMeU7P8hQgcorN-1SCyNagxCqV9p6YB1sAJWZ4uEhDAyCRA7nZXOCYNnZeBFvt5GhTsE5BpRXtwwvm4NTenIl_LiMSVlA\",             \"CmRaAAAAwQxqR91HU5kU5c5Krz1Rnf6L4rsD-rP577ZcjChcU4YXjk-DUlLyE03bMMHh-4_RVzx7Ta5Gp76azr1r6FJOoRBwV86FNDXgnIlOOwKd1hhRuDsrbugYsN-qWxjBj0JHEhAn5xPHU-s7hc4x2aLzyBOvGhSABtGmzFwrwk1KqcyTL0tyIlKUJQ\",             \"CmRaAAAAaTxiFlwPKGQMyDQMPIaWYtQg9D_8w1yp6Bb1T-_I9-_t1J4Gs27KPTLpG0Ox489F6C6mvjcPZdPf_UJTaGpNzSWyVdX7VTidZnOdSJVXPiK7RDDDiyRtFqCougTi8nKgEhDruopQTAK3eLRPqahNgUSZGhS1cTALUHKym9Ks-_eQi38JljIizQ\"         ],         \"city\": \"Los Angeles County\",         \"state\": \"CA\",         \"price\": \"\",         \"hours\": [             \"Monday: Closed\",             \"Tuesday: 12:00 – 9:30 PM\",             \"Wednesday: 12:00 – 9:30 PM\",             \"Thursday: 12:00 – 9:30 PM\",             \"Friday: 12:00 – 9:30 PM\",             \"Saturday: 12:00 AM – 9:30 PM\",             \"Sunday: 10:00 AM – 9:30 PM\"         ],         \"open_now\": true,         \"periods\": [             {                 \"close\": {                     \"day\": 0,                     \"time\": \"2130\"                 },                 \"open\": {                     \"day\": 0,                     \"time\": \"1000\"                 }             },             {                 \"close\": {                     \"day\": 2,                     \"time\": \"2130\"                 },                 \"open\": {                     \"day\": 2,                     \"time\": \"1200\"                 }             },             {                 \"close\": {                     \"day\": 3,                     \"time\": \"2130\"                 },                 \"open\": {                     \"day\": 3,                     \"time\": \"1200\"                 }             },             {                 \"close\": {                     \"day\": 4,                     \"time\": \"2130\"                 },                 \"open\": {                     \"day\": 4,                     \"time\": \"1200\"                 }             },             {                 \"close\": {                     \"day\": 5,                     \"time\": \"2130\"                 },                 \"open\": {                     \"day\": 5,                     \"time\": \"1200\"                 }             },             {                 \"close\": {                     \"day\": 6,                     \"time\": \"2130\"                 },                 \"open\": {                     \"day\": 6,                     \"time\": \"0000\"                 }             }         ]     } ]";
//      private  String curDet = "[ { \"id\": \"ChIJtUeFt3bDwoARdj3_2AProOg\", \"name\": \"True Food Kitchen\", \"address\": \"168 W Colorado Blvd, Pasadena, CA 91105, USA\", \"lat\": 34.14548060000001, \"lon\": -118.1539142, \"phone\": \"+1 626-639-6818\", \"zip\": \"91105\", \"website\": \"https://www.truefoodkitchen.com/pasadena\", \"rating\": 4.5, \"url\": \"https://maps.google.com/?cid=16762656214831021430\", \"google_reviews\": [ { \"auth_name\": \"Sara Deckers\", \"pic\": \"https://lh5.googleusercontent.com/-qizK_utUo8c/AAAAAAAAAAI/AAAAAAAAAjQ/xR14c2fs7vk/s128-c0x00000000-cc-rp-mo-ba4/photo.jpg\", \"rating\": 3, \"text\": \"The ambiance is great and the food is good. It's not mind blowing by any means. They could stand to think outside the box a little, but if you're looking for a safe vegetarian meal, it's a good option. They have happy hour too and the prices are decent, though again, the options could be a little more interesting.\", \"time\": 1521774409, \"url\": \"https://www.google.com/maps/contrib/110669144494744300378/reviews\" }, { \"auth_name\": \"Pierce Erica\", \"pic\": \"https://lh5.googleusercontent.com/-1H3g9ED3gAM/AAAAAAAAAAI/AAAAAAAAAAc/aKzL8Rzg9nU/s128-c0x00000000-cc-rp-mo/photo.jpg\", \"rating\": 5, \"text\": \"Amazing food! I didn't try the coffee but people walked in there and stood in a long line just for coffee so I'm guessing it's good ! Very small place so be prepared to wait for a spot if you go during their busy times. Overall, I would recommend this cute place and I can't wait to try everything on the menu.\", \"time\": 1524042562, \"url\": \"https://www.google.com/maps/contrib/114432918056552000656/reviews\" }, { \"auth_name\": \"Sandra Vierra\", \"pic\": \"https://lh4.googleusercontent.com/-tJggBLWUEgo/AAAAAAAAAAI/AAAAAAAAACQ/kXEj9870DYU/s128-c0x00000000-cc-rp-mo/photo.jpg\", \"rating\": 4, \"text\": \"True “Good” Food at this kitchen is what I got. My ginger margarita was wonderful. The best part of my experience, however, was my waitress. She was attentive, kind, but not overbearing. I wish I could remember her name because the service she gave was exemplary.\", \"time\": 1519608342, \"url\": \"https://www.google.com/maps/contrib/115878614049491905930/reviews\" }, { \"auth_name\": \"Carl B\", \"pic\": \"https://lh6.googleusercontent.com/-YfJumwE_YXA/AAAAAAAAAAI/AAAAAAAAeFA/zwoAkB-JYKk/s128-c0x00000000-cc-rp-mo/photo.jpg\", \"rating\": 5, \"text\": \"Finally somebody in this space to keep it hopping. (I miss the old oyster bar that was here when there wasn't even a cheesecake factory in 'Old Town' yet.). Food is delicious. Love the ginger margaritas! The space is big: not cozy. It gets crowded, busy (reservations recommended); sometimes I feel a bit lost in the crowd. The staff has generally been pretty good though. Feels like typical \\\"L.A.\\\" scene to me - like most other options nearby (try Santorini's for something else a little different - out of the way but still central - and maybe with a more personal feel.) But I go back to True Food for lunch most of the time when I'm in Pasadena.\", \"time\": 1519852715, \"url\": \"https://www.google.com/maps/contrib/104217237509965296128/reviews\" }, { \"auth_name\": \"Cyrus Nabati\", \"pic\": \"https://lh4.googleusercontent.com/-nAlPKB9baYo/AAAAAAAAAAI/AAAAAAAAAGE/xAASdc9Y6D0/s128-c0x00000000-cc-rp-mo/photo.jpg\", \"rating\": 5, \"text\": \"5 Star for great food and 4.5 for service which was kinda slow! Pretty simple menu to choose from with great quality and organic ingredients/taste! Indoor & outdoor seating. Public parking available a short walking block away.\", \"time\": 1524180327, \"url\": \"https://www.google.com/maps/contrib/113470469533968725806/reviews\" } ], \"yelp_reviews\": [ { \"auth_name\": \"Natalie T.\", \"pic\": \"https://s3-media4.fl.yelpcdn.com/photo/ax7KBNZXxgcCL0Top-iIfg/o.jpg\", \"rating\": 5, \"text\": \"What a beautiful restaurant! my husband and I went here to celebrate our anniversary had a fantastic experience. Our wonderful server who I sadly can't...\", \"time\": \"2018-04-15 13:33:30\", \"url\": \"https://www.yelp.com/biz/true-food-kitchen-pasadena-2?hrid=q7xRScLDdGDqd8I6_dHE7Q&adjust_creative=cV_HQJecOy0sntVDvTsEaw&utm_campaign=yelp_api_v3&utm_medium=api_v3_business_reviews&utm_source=cV_HQJecOy0sntVDvTsEaw\" }, { \"auth_name\": \"LC J.\", \"pic\": \"https://s3-media1.fl.yelpcdn.com/photo/TkdjXdEzHpWeyrbnzQOAMg/o.jpg\", \"rating\": 1, \"text\": \"Service is horrible, understaffed on weekends when they should know damn well it's a good location and in the heart of Pasadena, and they'll make you wait...\", \"time\": \"2018-04-21 12:41:50\", \"url\": \"https://www.yelp.com/biz/true-food-kitchen-pasadena-2?hrid=qlBh_RoEKVamfhY8tMGsrQ&adjust_creative=cV_HQJecOy0sntVDvTsEaw&utm_campaign=yelp_api_v3&utm_medium=api_v3_business_reviews&utm_source=cV_HQJecOy0sntVDvTsEaw\" }, { \"auth_name\": \"Simon C.\", \"pic\": \"https://s3-media3.fl.yelpcdn.com/photo/fU0zJz9NeVYnEaOFOaMcCQ/o.jpg\", \"rating\": 4, \"text\": \"Food here is yummy, service is good too. \\n\\nWe had the Farmers Market crudités, which is huge, so make sure you are hungry when you order it! It was almost a...\", \"time\": \"2018-04-20 22:15:00\", \"url\": \"https://www.yelp.com/biz/true-food-kitchen-pasadena-2?hrid=dKpF0_Ao4XeNEADjocysqA&adjust_creative=cV_HQJecOy0sntVDvTsEaw&utm_campaign=yelp_api_v3&utm_medium=api_v3_business_reviews&utm_source=cV_HQJecOy0sntVDvTsEaw\" } ], \"photos\": [ \"CmRaAAAAXvMC1gholncK49isBA2VXItft5XQdoXTinQz1k6GHzrI4ne0xZVCn34eHt8JILgHDTRV6o_euRgrtXN3Vf3vtxb-YL0iLFvjskpJY8vX3NAZxiB3fX7wzgIGgwD54IX7EhAo895rVWhauyinW5k5E3DoGhTy2NIhAI65-71poSpkHbcD9Y-NVg\", \"CmRaAAAATuIIv5TyR1AvmuxJyhqD7XQY2Xkc4QXPkPn0dwGzkWSSOSE9Qs855E1Ubpkr6Vb_q7-_zVSsVmcWg977d0_wCGpHrTcyP33_f76lObm640ya61i6-nAMmq78RDJcHmXoEhBTX3w8IIySWTyOKqFaB-vaGhRjrJZbN6eQwEDfyHqditVAsaTTMA\", \"CmRaAAAAsl5sphfJJAI02CjF8jefABVIGZJU7Kkk-IikMHcXb56KC2fAvhlxxTeurHIMJDvLBCFpLpx7gL1eFsLMfeVQEH3ADCTy_EMtdesRPdS_NmYpqqfJPM32aUMYKf0JXzI4EhBv9QcNF6k2iNX6Be7HSP-YGhSWwBADnyMU_gN3z0X7tKSKUObnqQ\", \"CmRaAAAAxr_igVXmIZLevzD3S40KUYd_8qgyApHaVLseWgCqErqjGnhSs2BR3VWXSTw1xsa_3pAvv5b4UAuopWym4fDxYEj8bJYbOW_tZWRq8fqO0c_qeKzNWL_ff78W-diamX3aEhAJWBqa01s5fS-ZbhX4Pz21GhSDt8KUZZFKtxdoUorer59DbGvLNQ\", \"CmRaAAAAtPZfoQJqL8VORjsymzCBn1ehznOa9ZaAfPdQKAxjkcwYdHKYYCVhbox7uGAmMpq9_UgIbahQRXOrm2phaA4Qj3VVMnoYlfRWNhMdsdhyJV5Oq4MqipV4WnTq86JbrhVxEhAADLYAln7jz1-wdunw2-xxGhSqlA0d7tsZA6s_fY9N8LdnwuTQcg\", \"CmRaAAAAlL58AMsWuE3SpJl55X8dhdWorypzsnPso8Bf59vNLqbYtRnRWfeK2TMHXH_OFAWxEICpIjjyGckjas2_7PlCcz3xvtZB3vDaAm6AmYSU70NNRYMJLszdA0rRBCCVDizjEhDYHz_93BEu-mw2TeU9bsC5GhSwU-xAJdOK1Z8g32aKQisum58OVQ\", \"CmRaAAAATDwgWvFPnfjyaETHWuD2TxxXkB_w7aDGCTgbpH6Ah_xpHbyjCsGSrQEO0A8jPds_8p_HFARyKZWMDseAohk2HDnALUsizU33zgrN8JuIoGA2M954WwItQ2mvvVruA16YEhBubdcplEJ9gwOLeeFNca4jGhRGNriK04DenQ_GbGQxw7VbaucjoQ\", \"CmReAAAAFtYFMCQluLq3V5Gv9mBoxUS7yB3jOoJ56C4QGmn5tQ7ABH3JBbjK8T-EpgwlvOpoDjTnYj_bUudFgroRrHxrtZ7lm-gVhxMdvRnt2dlGGc8hvGwcZPMUK___ySqB9mqVEhBm2U50jprm9yzfWI-onIh4GhQfepbp9kn3gpgRBqMChu_fuYm0rg\", \"CmRaAAAAT34SJdxlL5LNDrzwRh01t_XwQOwdxBpEjuRNrvswKPiPyJMqJCa3FS8OQm8Z9McXsMPv1v035v-8fQ7x0rZ3owz5Lv6E-8iZ6NVq_Zkqyn1VHEU6x7IEFjZqpPlxpwiJEhDjblVZV4i0fQ79gqLTlmDTGhRRt3fTfNEgDElOgs3lFZXb7UtwBA\", \"CmRaAAAAC6HyfFLDf5O4MNlS1x8s34Rv5RSOIqyLQ3SipHbn-Zd92h36pWiahNh5D04jh6RAhoTW53oYjm1FzrfIgZ0NhwRLaNJcgp1unVEF3zSJ6LveHFG45rQZUOaj5C1QFdq3EhCAX2fY7mpfmXgirlazPCgMGhQuaZwO81EzJnXGpmEjmqKsikzYVw\" ], \"city\": \"Los Angeles County\", \"state\": \"CA\", \"price\": 2, \"hours\": [ \"Monday: 11:00 AM – 9:00 PM\", \"Tuesday: 11:00 AM – 9:00 PM\", \"Wednesday: 11:00 AM – 9:00 PM\", \"Thursday: 11:00 AM – 9:00 PM\", \"Friday: 11:00 AM – 10:00 PM\", \"Saturday: 10:00 AM – 10:00 PM\", \"Sunday: 10:00 AM – 9:00 PM\" ], \"open_now\": true, \"periods\": [ { \"close\": { \"day\": 0, \"time\": \"2100\" }, \"open\": { \"day\": 0, \"time\": \"1000\" } }, { \"close\": { \"day\": 1, \"time\": \"2100\" }, \"open\": { \"day\": 1, \"time\": \"1100\" } }, { \"close\": { \"day\": 2, \"time\": \"2100\" }, \"open\": { \"day\": 2, \"time\": \"1100\" } }, { \"close\": { \"day\": 3, \"time\": \"2100\" }, \"open\": { \"day\": 3, \"time\": \"1100\" } }, { \"close\": { \"day\": 4, \"time\": \"2100\" }, \"open\": { \"day\": 4, \"time\": \"1100\" } }, { \"close\": { \"day\": 5, \"time\": \"2200\" }, \"open\": { \"day\": 5, \"time\": \"1100\" } }, { \"close\": { \"day\": 6, \"time\": \"2200\" }, \"open\": { \"day\": 6, \"time\": \"1000\" } } ], \"utc_offset\": -420 } ]";


        public boolean is_InFavTab(){
          return this.isInFavTab;
        }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView address;
        public ImageView icon;
        public ImageView fav_icon;

        public ViewHolder(View view) {

            super(view);

            name = view.findViewById(R.id.name);
            address = view.findViewById(R.id.address);
            icon = view.findViewById(R.id.icon);
            fav_icon = view.findViewById(R.id.fav_icon);
        }
    }

    public PlacesRecyclerViewAdapter(List<Place> list, Context ctx, boolean isInFavTab) {
        placesList = list;
        System.out.println("Size of placelist: "+ placesList.size());
        context = ctx;
        this.isInFavTab = isInFavTab;
    }
    @Override
    public int getItemCount() {
        return placesList.size();
    }

    @Override
    public PlacesRecyclerViewAdapter.ViewHolder
    onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.places_item, parent, false);

        PlacesRecyclerViewAdapter.ViewHolder viewHolder =
                new PlacesRecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PlacesRecyclerViewAdapter.ViewHolder holder, final int position) {
        final int itemPos = position;
        final Place place = placesList.get(position);
        holder.name.setText(place.getPlaceName());
        holder.address.setText(place.getPlaceAddress());
        //holder.icon.setText(place.getPlaceIcon());
        new DownloadImageTask(holder.icon)
                .execute(place.getPlaceIcon());

        System.out.println("Cur index: " + position + " place: " + place );

        if (  FavsList.getInstance().isFav( place.getPlaceId() ) )
            holder.fav_icon.setImageResource(R.drawable.heart_fill_red);
        else
             holder.fav_icon.setImageResource(R.drawable.heart_outline_black) ;


    // when you click this demo button
        holder.fav_icon.setOnClickListener(new View.OnClickListener() {
               public void onClick(View v) {
                   if ( FavsList.getInstance().isFav(place.getPlaceId()) ) {
                       holder.fav_icon.setImageResource(R.drawable.heart_outline_black);
                       FavsList.getInstance().removeFav(place.getPlaceId());
                       System.out.println("Favs size: " + FavsList.getInstance().getSize());
                       System.out.println("Favs : " + FavsList.getInstance());
                       Toast.makeText(context, "Removed " + place.getPlaceName()+ " from favorites", Toast.LENGTH_SHORT).show();
                   } else {
                       holder.fav_icon.setImageResource(R.drawable.heart_fill_red);
                       FavsList.getInstance().addFav(place);
                       System.out.println("Favs size: " + FavsList.getInstance().getSize());
                       System.out.println("Favs : " + FavsList.getInstance());
                       Toast.makeText(context, "Added " + place.getPlaceName()+ " to favorites", Toast.LENGTH_SHORT).show();
                   }

                   if (isInFavTab){
                       removeAt(position);
                       //https://stackoverflow.com/questions/26076965/android-recyclerview-addition-removal-of-items
                   }
               }
           });


        holder.address.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getPlaceDetails(place);
                loadProgressDialog();
            }
        });

        holder.name.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getPlaceDetails(place);
                loadProgressDialog();
            }
        });


    }


    public void removeAt(int position) {
        placesList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, placesList.size());
        if (placesList.size() == 0 ){
            Toast.makeText(context, "No Favorites Found.", Toast.LENGTH_SHORT).show();
        }
    }

    public  void  getPlaceDetails(final Place curPlace ){

        String url = "http://csci571hw8-198219.appspot.com/?details=1&id=" + curPlace.getPlaceId();
        CharSequence text = "Deatils URL: " + url;
        int duration = Toast.LENGTH_LONG;
        //Toast toast = Toast.makeText(context, text, duration);
        //toast.show();


/*        try {
            JSONArray details = new JSONArray(curDet);
            System.out.println("curDet: " + details);
            Intent detIntent = new Intent(context, DetailsActivity.class);
            //detIntent.putExtra("details", new Detail(details) );
            Detail.getInstance().addDetail(details);
            Detail.getInstance().setPlaceObj(curPlace);
            //loadProgressDialog();
            context.startActivity(detIntent);


        } catch (JSONException e) {
            e.printStackTrace();
        } */


        RequestQueue queue = Volley.newRequestQueue(context);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.toString());
                        try {
                            //JSONArray parser = new JSONArray(response);
                            JSONArray details = new JSONArray(response);
                            System.out.println("curDet: " + details);
                            Intent detIntent = new Intent(context, DetailsActivity.class);
                            //detIntent.putExtra("isInFavTab", is_InFavTab() );
                            Detail.getInstance().addDetail(details);
                            Detail.getInstance().setPlaceObj(curPlace);

                            progressDiag.dismiss();

                            //loadProgressDialog();
                            context.startActivity(detIntent);




                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("Couldn't convert string response to json");
                            progressDiag.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
                Log.i(TAG, "Request Failed" + error );
                progressDiag.dismiss();

            }

        });


// Add the request to the RequestQueue.
        queue.add(stringRequest);



    }





    //Downloads the images icons
    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    public void loadProgressDialog(){
        //https://www.journaldev.com/9652/android-progressdialog-example
        progressDiag = new ProgressDialog(context);
        progressDiag.setMax(100);
        progressDiag.setMessage("Fetching results");
        progressDiag.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDiag.show();

    }


}