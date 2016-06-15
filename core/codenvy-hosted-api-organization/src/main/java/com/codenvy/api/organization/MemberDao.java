/*
 *  [2012] - [2016] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.api.organization;

import com.codenvy.api.organization.model.Member;
import com.codenvy.api.organization.model.impl.MemberImpl;
import com.google.common.base.Objects;
import com.google.common.reflect.TypeToken;

import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.local.storage.LocalStorage;
import org.eclipse.che.api.local.storage.LocalStorageFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * @author Sergii Leschenko
 */
public class MemberDao {
    private final LocalStorage         memberStorage;
    public final  Map<Key, MemberImpl> members;

    public MemberDao(LocalStorageFactory storageFactory) throws IOException {
        this.members = new HashMap<>();
        this.memberStorage = storageFactory.create("members.json");
    }

    @PostConstruct
    public void load() {
        final List<MemberImpl> members = memberStorage.loadList(new TypeToken<List<MemberImpl>>() {});
        final Map<Key, MemberImpl> membersMap = members.stream()
                                                       .collect(toMap(Key::valueOf, member -> member));
        this.members.putAll(membersMap);
    }

    @PreDestroy
    public void save() throws IOException {
        memberStorage.store(new ArrayList<>(members.values()));
    }

    /**
     * Stores (adds or updates) member.
     *
     * @param member
     *         member to store
     */
    public void store(MemberImpl member) {
        members.put(Key.valueOf(member), member);
    }

    /**
     * Returns all members of given organization
     *
     * @param organization
     *         id of organization
     */
    public List<MemberImpl> getMembers(String organization) {
        return members.values()
                      .stream()
                      .filter(member -> member.getOrganization().equals(organization))
                      .collect(toList());
    }

    /**
     * Remove member with given organization and user
     *
     * @param organization
     *         id of organization
     * @param user
     *         id of user
     */
    public void removeMember(String organization, String user) throws NotFoundException {
        final MemberImpl removedMember = members.remove(Key.valueOf(organization, user));
        if (removedMember == null) {
            throw new NotFoundException("Member was not found");
        }
    }

    public MemberImpl getMember(String organization, String user) throws NotFoundException {
        final MemberImpl member = members.get(Key.valueOf(organization, user));
        if (member == null) {
            throw new NotFoundException("Member was not found");
        }
        return member;
    }

    public List<MemberImpl> getMemberships(String user) {
        return members.values()
                      .stream()
                      .filter(member -> member.getUser().equals(user))
                      .collect(toList());
    }

    private static class Key {
        private String organization;
        private String user;

        public Key(String organization, String user) {
            this.organization = organization;
            this.user = user;
        }

        public static Key valueOf(String organization, String user) {
            return new Key(organization, user);
        }

        public static Key valueOf(Member member) {
            return new Key(member.getOrganization(), member.getUser());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            Key key = (Key)o;
            return Objects.equal(organization, key.organization) &&
                   Objects.equal(user, key.user);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(organization, user);
        }
    }
}
