package orzu.org.models;

import java.util.List;


import io.reactivex.Observable;
import orzu.org.Bonuses;
import orzu.org.category_model;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MyTaskApi {
    @GET("/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_task&tasks=all")
    Observable<List<MyTasks>> getData(@Query("userid") String id, @Query("page") String count);

    @GET("/api?%20appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_cat&requests=no&cat_id=only_parent")
    Observable<List<category_model>> getCategories();

    @GET("/api?%20appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_cat&cat_id=only_subcat")
    Observable<List<category_model>> getSubcategories(@Query("id") String id);

    @GET("/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=user_param&act=partners_list_all")
    Observable<List<Bonuses>> getAllPartners();

}
