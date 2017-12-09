
// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Author: Claudius Korzen <korzen@cs.uni-freiburg.de>.

import java.util.ArrayList;
import java.util.List;

/**
 * An entity in the q-gram index.
 */
public class Entity {
  /**
   * The name of this entity.
   */
  public String name;

  /**
   * The score of this entity.
   */
  public int score;

  /**
   * The description of this entity.
   */
  public String desc;

  /**
   * The Wikipedia url of this entity.
   */
  public String wikipediaUrl;

  /**
   * The Wikidata id of this entity.
   */
  public String wikidataId;

  /**
   * The synonyms of this entity.
   */
  public List<String> synonyms;

  /**
   * The image URL of this entity.
   */
  public String imageUrl;

  /**
   * The prefix edit distance when this entity is part of a query result.
   */
  public int ped = -1;
  
  /**
   * The matched synonym when this entity is part of a query result due to a
   * matching synonym.
   */
  public String matchedSynonym;

  /**
   * Creates a new entity.
   *
   * @param name
   *        The name of the entity.
   * @param score
   *        The score of the entity.
   */
  public Entity(String name, int score) {
    this.name = name;
    this.score = score;
  }

  /**
   * Creates a new entity.
   *
   * @param name
   *        The name of the entity.
   * @param score
   *        The score of the entity.
   * @param description
   *        The description of the entity.
   * @param wikipediaUrl
   *        The Wikipedia URL of the entity.
   * @param wikidataId
   *        The Wikidata id of the entity.
   * @param synonyms
   *        The synonyms of the entity.
   * @param imageUrl
   *        The URL of this image.
   */
  public Entity(String name, int score, String description,
      String wikipediaUrl, String wikidataId, List<String> synonyms,
      String imageUrl) {
    this.name = name;
    this.score = score;
    this.desc = description;
    this.wikipediaUrl = wikipediaUrl;
    this.wikidataId = wikidataId;
    this.synonyms = synonyms;
    this.imageUrl = imageUrl;
  }

  @Override
  public String toString() {
    List<String> parts = new ArrayList<>();
    if (this.name != null) {
      parts.add("name='" + this.name + "'");
    }
    if (this.score > 0) {
      parts.add("score=" + this.score);
    }
    if (this.desc != null) {
      parts.add("desc='" + this.desc + "'");
    }
    if (this.ped != -1) {
      parts.add("ped=" + this.ped);
    }
    if (this.matchedSynonym != null) {
      parts.add("matchedSynonym='" + this.matchedSynonym + "'");
    }
    if (!this.imageUrl.equals("")) {
      parts.add("imageUrl='" + this.imageUrl + "'");
    }
    return String.format("Entity(%s)", String.join(", ", parts));
  }
}