package com.wishingfu.tools.fileshare.mapper;

import com.wishingfu.tools.fileshare.model.LocalFile;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LocalFileMapper {

    @Select("select * from t_local_file")
    List<LocalFile> selectAll();

    @Select("select * from t_local_file where id = #{id}")
    LocalFile selectById(@Param("id") String id);

    @Delete("delete from t_local_file where origin_name = #{name}")
    int deleteFile(@Param("name") String name);

    @Insert("""
            insert into t_local_file(id, origin_name, file_size, sha256, upload_time) values
            (
                #{id}, #{originName}, #{fileSize}, #{sha256}, #{uploadTime}
            )
            """)
    int insertFile(LocalFile file);

    @Select("select * from t_local_file where origin_name = #{fileName}")
    LocalFile selectByName(@Param("fileName") String fileName);
}
