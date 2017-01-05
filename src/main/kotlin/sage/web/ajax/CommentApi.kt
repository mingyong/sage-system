package sage.web.ajax

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sage.entity.Comment
import sage.service.BlogService
import sage.service.TweetPostService
import sage.transfer.CommentView
import sage.web.auth.Auth

@RestController
@RequestMapping("/comments")
open class CommentApi @Autowired constructor(
    private val tweetPostService: TweetPostService,
    private val blogService: BlogService) {

  @RequestMapping("/new")
  open fun create(@RequestParam content: String, @RequestParam sourceType: Short, @RequestParam sourceId: Long,
                  @RequestParam(required = false) replyUserId: Long?,
                  @RequestParam(required = false) forward: Boolean?) {
    val uid = Auth.checkUid()
    if (sourceType == Comment.TWEET) {
      tweetPostService.comment(uid, content, sourceId, replyUserId)
      if (forward ?: false) {
        tweetPostService.forward(uid, content, sourceId, emptyList())
      }
    } else if (sourceType == Comment.BLOG) {
      blogService.comment(uid, content, sourceId, replyUserId)
    }
  }

  @RequestMapping
  open fun comments(@RequestParam sourceType: Short, @RequestParam sourceId: Long): Map<String, Any> {
    return mapOf("count" to Comment.count(sourceType, sourceId),
        "list" to Comment.list(sourceType, sourceId).map(::CommentView))
  }
}