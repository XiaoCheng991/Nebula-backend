-- ============================================
-- 博客功能 - 为日常碎碎念添加字段
-- 在现有的blog_article表上添加碎碎念所需的字段
-- ============================================

-- 添加心情字段
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='blog_article' AND column_name='mood') THEN
        ALTER TABLE blog_article ADD COLUMN mood VARCHAR(50);
        COMMENT ON COLUMN blog_article.mood IS '心情（happy, sad, excited, calm, anxious等）';
    END IF;
END $$;

-- 添加位置字段
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='blog_article' AND column_name='location') THEN
        ALTER TABLE blog_article ADD COLUMN location VARCHAR(200);
        COMMENT ON COLUMN blog_article.location IS '位置';
    END IF;
END $$;

-- 添加天气字段
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='blog_article' AND column_name='weather') THEN
        ALTER TABLE blog_article ADD COLUMN weather VARCHAR(50);
        COMMENT ON COLUMN blog_article.weather IS '天气';
    END IF;
END $$;

-- 添加标签字段
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='blog_article' AND column_name='tags') THEN
        ALTER TABLE blog_article ADD COLUMN tags VARCHAR(500);
        COMMENT ON COLUMN blog_article.tags IS '标签（多个标签用逗号分隔）';
    END IF;
END $$;

-- 添加是否公开字段
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='blog_article' AND column_name='is_public') THEN
        ALTER TABLE blog_article ADD COLUMN is_public BOOLEAN NOT NULL DEFAULT true;
        COMMENT ON COLUMN blog_article.is_public IS '是否公开';
    END IF;
END $$;

-- 为新添加的字段创建索引
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='blog_article' AND indexname='idx_blog_article_mood') THEN
        CREATE INDEX idx_blog_article_mood ON blog_article(mood);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='blog_article' AND indexname='idx_blog_article_location') THEN
        CREATE INDEX idx_blog_article_location ON blog_article(location);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='blog_article' AND indexname='idx_blog_article_weather') THEN
        CREATE INDEX idx_blog_article_weather ON blog_article(weather);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='blog_article' AND indexname='idx_blog_article_tags') THEN
        CREATE INDEX idx_blog_article_tags ON blog_article(tags);
    END IF;
END $$;

-- 验证
SELECT '博客碎碎念字段添加完成！' AS status;