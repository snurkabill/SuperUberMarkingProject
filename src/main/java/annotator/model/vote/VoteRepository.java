package annotator.model.vote;

import annotator.model.AbstractRepository;
import annotator.model.word.Word;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class VoteRepository extends AbstractRepository {

    private final MongoCollection<Document> votes;

    public VoteRepository(MongoDatabase database) {
        this.votes = database.getCollection("votes");
    }

    public void addVote(String userId, Word word, Boolean voteResult, Integer duration) {
        Document newVote = new Document("_id", new ObjectId())
            .append("user_id", userId)
            .append("word_id", word.getId())
            .append("type_id", word.getTypeId())
            .append("belongs_to_type", word.belongsToType())
            .append("vote_belongs_to_type", voteResult)
            .append("duration", duration);
        this.votes.insertOne(newVote);
    }

    private List<Vote> getVotes(MongoCursor<Document> cursor) {
        List<Vote> votes = new ArrayList<>();
        while (cursor.hasNext()) {
            votes.add(new Vote(cursor.next()));
        }
        cursor.close();
        return votes;
    }

    public List<Vote> getVotesByWordId(String wordId) {
        MongoCursor<Document> cursor = this.votes.find(
            Filters.eq("word_id", wordId)
        ).iterator();
        return this.getVotes(cursor);
    }

    public List<Vote> getAllVotesByUserId(String userId) {
        MongoCursor<Document> cursor = this.votes.find(
            Filters.eq("user_id", userId)
        ).iterator();
        return this.getVotes(cursor);
    }

    public List<Vote> getAllVotes() {
        MongoCursor<Document> cursor = this.votes.find().iterator();
        return this.getVotes(cursor);
    }

    public List<Vote> getAllNoisyVotes() {
        MongoCursor<Document> cursor = this.votes.find(
                Filters.not(
                        Filters.eq("belongs_to_type", null)
                )
        ).iterator();
        return this.getVotes(cursor);
    }

    public List<Vote> getNoisyVotesByUserId(String userId) {
        MongoCursor<Document> cursor = this.votes.find(
            Filters.and(
                Filters.eq("user_id", userId),
                Filters.not(
                    Filters.eq("belongs_to_type", null)
                )
            )
        ).iterator();
        return this.getVotes(cursor);
    }

    public List<Vote> getVotesByPackageId(String packageId) {
        MongoCursor<Document> cursor = this.votes.find(
                Filters.eq("package_id", packageId)
        ).iterator();
        return this.getVotes(cursor);
    }

}
