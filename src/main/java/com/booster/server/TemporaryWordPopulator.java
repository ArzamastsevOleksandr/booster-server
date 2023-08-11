package com.booster.server;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Component
@Deprecated(forRemoval = true, since = "Replace with an export/import feature")
@RequiredArgsConstructor
class TemporaryWordPopulator implements CommandLineRunner {

    final VocabularyEntryService vocabularyEntryService;
    final NoteService noteService;

    // todo: integrate with an online dictionary? fetch alternatives and select the name, desc and synonyms?
    @Override
    public void run(String... args) {
        addVocabularyEntries();
        addNotes();
    }

    private void addNotes() {
        addN("Saying what you mean and meaning what you say");
        addN("If it isn't a clear yes, then it's a clear no");
        addN("Less but better");
        addN("The main thing is to keep the main thing the main thing");
        addN("Pause before you speak");
        addN("I saw the angel in the marble and carved until I set him free");
        addN("No is a complete sentence");
        addN("Done is better than perfect");
        addN("Simple is better than complex. Complex is better than complicated");
        addN("Distinguish the vital few from the trivial many");
        addN("Living by design, not by default");
        addN("The faintest pencil is better than the strongest memory");
        addN("A little nonsense now and then is cherished by the wisest men");
        addN("Your health is your greatest wealth");
    }

    private void addN(String content) {
        noteService.create(new CreateNoteInput().setContent(content));
    }

    private void addVocabularyEntries() {
        addVe("poignant", "evoking a keen sense of sadness or regret", Set.of("touching"));
        addVe("mutter", "say smth in a low voice in irritation", Set.of("grumble"));
        addVe("salient", null, Set.of("important"));
        addVe("salient", null, Set.of("important"));
        addVe("renegade", null, Set.of("traitor"));
        addVe("audacity", "a willingness to take bold risks", Set.of("boldness", "impudence"));
        addVe("audacity", "a willingness to take bold risks; rude or disrespectful behavior", Set.of("boldness", "impudence"));
        addVe("irrevocable", null, Set.of("irreversible"));
        addVe("ordeal", "painful/unpleasant experience", Set.of());
        addVe("facetious", "treating serious issues with deliberately inappropriate humour", Set.of("flippant"));
        addVe("mingle", null, Set.of("mix", "socialize"));
        addVe("hideous", null, Set.of("ugly", "horrific"));
        addVe("ajar", null, Set.of("slightly open"));
        addVe("ajar", null, Set.of("slightly open"));
        addVe("futility", null, Set.of("pointlessness", "uselessness", "fruitlessness"));
        addVe("tender", null, Set.of("caring", "affectionate"));
    }

    private void addVe(String name, String description, Set<String> synonyms) {
        ThreadLocalRandom current = ThreadLocalRandom.current();

        VocabularyEntryDto vocabularyEntryDto = vocabularyEntryService.create(new CreateVocabularyEntryInput()
                .setName(name)
                .setDescription(description)
                .setSynonyms(synonyms));

        vocabularyEntryService.updateLastSeenAt(new UpdateLastSeenAtInput()
                .setIds(List.of(vocabularyEntryDto.getId()))
                .setLastSeenAt(LocalDateTime.now().minusHours(current.nextInt(1000))));

        IntStream.rangeClosed(0, current.nextInt(10))
                .forEach(i -> vocabularyEntryService.updateCorrectAnswersCount(new UpdateCorrectAnswersCountInput()
                        .correct(true)
                        .id(vocabularyEntryDto.getId())));
    }

}
