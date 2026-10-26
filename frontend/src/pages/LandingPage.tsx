import { ArrowUpRight, CalendarDays, MapPin, Quote, Trophy, Users } from 'lucide-react';
import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { endpoints } from '../lib/api';
import { ArrowLink, EmptyState, Loading } from '../components/Layout';
import type { Club, Event } from '../types';

const campusImages = {
  hero: 'https://images.unsplash.com/photo-1564981797816-1043664bf78d?auto=format&fit=crop&w=1400&q=85',
  community: 'https://images.unsplash.com/photo-1523240795612-9a054b0db644?auto=format&fit=crop&w=1100&q=85',
  workshop: 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=900&q=85',
  performance: 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=900&q=85',
};

export function LandingPage() {
  const clubs = useQuery({ queryKey: ['clubs'], queryFn: () => endpoints.clubs() });
  const events = useQuery({ queryKey: ['events'], queryFn: endpoints.events });
  const edition = useQuery({ queryKey: ['active-edition'], queryFn: endpoints.activeEdition });
  const upcomingEvents = (events.data || []).filter((event) => new Date(event.startsAt).getTime() >= Date.now()).slice(0, 3);

  return <div className="landing-page">
    <section className="editorial-hero">
      <div className="editorial-hero-copy">
        <div className="eyebrow light"><span className="eyebrow-dot" />One campus, every club</div>
        <h1>Make room for<br /><em>what moves you.</em></h1>
        <p>Clubbies is the campus guide for finding your people, showing up to what matters, and turning small ideas into shared memories.</p>
        <Link className="button button-primary" to="/clubs">Explore the campus <ArrowUpRight size={17} /></Link>
        <div className="hero-caption"><span>Clubbies / 01</span><span>Student life, gathered</span></div>
      </div>
      <figure className="editorial-hero-image">
        <img src={campusImages.hero} alt="Students walking together across a university campus" />
        <figcaption><span>Find your circle</span><span>01—04</span></figcaption>
      </figure>
    </section>

    <section className="intro-editorial section">
      <div className="section-kicker">A calmer campus directory</div>
      <div className="intro-editorial-grid">
        <h2>The best parts of campus are usually found <em>between the lines.</em></h2>
        <div><p>One shared place for club identities, upcoming events, recruitment, and opportunities to participate. No scattered forms. No guessing where to start.</p><ArrowLink to="/clubs">Start exploring</ArrowLink></div>
      </div>
    </section>

    <section className="campus-journal section">
      <div className="section-heading-row"><div><div className="section-kicker">The campus journal</div><h2>There is a place<br /><em>for your energy.</em></h2></div><p className="section-aside">Browse communities by the way they feel: curious, competitive, creative, thoughtful, or simply open to something new.</p></div>
      <div className="journal-grid">
        <article className="journal-feature"><img src={campusImages.community} alt="Students collaborating around a table" /><div className="journal-overlay"><span>Community / 01</span><h3>Bring an idea.<br />Find a team.</h3><Link to="/clubs">Meet the clubs <ArrowUpRight size={15} /></Link></div></article>
        <article className="journal-note"><div className="journal-note-number">02</div><Trophy size={25} strokeWidth={1.5} /><h3>Make a season<br /><em>of showing up.</em></h3><p>From the first practice to President Cup, participation is easier when the details live together.</p><Link className="text-link" to="/president-cup">See President Cup <ArrowUpRight size={15} /></Link></article>
        <article className="journal-image"><img src={campusImages.workshop} alt="Students working together on a laptop" /><div className="image-label">Create / collaborate / repeat</div></article>
      </div>
    </section>

    <section className="clubs-editorial section">
      <div className="section-heading-row"><div><div className="section-kicker">Featured communities</div><h2>People make<br /><em>the place.</em></h2></div><ArrowLink to="/clubs">View all clubs</ArrowLink></div>
      {clubs.isLoading ? <Loading /> : clubs.data?.length ? <div className="featured-clubs-grid">{clubs.data.slice(0, 3).map((club, index) => <FeaturedClub key={club.id} club={club} index={index} />)}</div> : <EmptyState title="Clubs are being curated" text="The directory is ready for your campus communities." />}
    </section>

    <section className="events-editorial section">
      <div className="events-editorial-header"><div><div className="section-kicker">On the calendar</div><h2>Make plans<br /><em>worth keeping.</em></h2></div><div className="events-editorial-copy"><p>Published events from across campus, with the time, venue, and context you need before you say yes.</p><ArrowLink to="/events">Open the shared calendar</ArrowLink></div></div>
      {upcomingEvents.length ? <div className="upcoming-editorial-list">{upcomingEvents.map((event) => <UpcomingEvent key={event.id} event={event} clubs={clubs.data || []} />)}</div> : <EmptyState title="The calendar is quiet for now" text="Approved events will appear here when clubs publish their next plans." />}
    </section>

    <section className="spotlight-section section">
      <div className="spotlight-image"><img src={campusImages.performance} alt="Students performing under warm stage lights" /><span>Community spotlight / 03</span></div>
      <div className="spotlight-copy"><Quote size={27} strokeWidth={1.5} /><div className="section-kicker">Stories worth sharing</div><h2>Small rooms can hold<br /><em>big beginnings.</em></h2><p>Every club has a first meeting, a brave idea, and a moment when strangers become a team. Clubbies gives those moments a visible place on campus.</p><Link className="button button-dark" to="/auth">Start your chapter <ArrowUpRight size={16} /></Link></div>
    </section>

    <section className="president-editorial section"><div className="president-editorial-copy"><div className="eyebrow light">A campus stage for sport</div><h2>President<br /><em>Cup.</em></h2><p>{edition.data?.introduction || 'Competition, teamwork, and the kind of moments campus remembers.'}</p><Link className="button button-light" to="/president-cup">Enter President Cup <ArrowUpRight size={16} /></Link></div><div className="president-editorial-mark"><span>PC</span><small>{edition.data?.name || 'SPORTS EDITION'}</small></div></section>

    <section className="closing-editorial section"><div className="section-kicker">Your next page</div><h2>There is more to campus<br /><em>than your timetable.</em></h2><Link className="button button-primary" to="/clubs">Find what is yours <ArrowUpRight size={17} /></Link></section>
  </div>;
}

function FeaturedClub({ club, index }: { club: Club; index: number }) {
  const images = [campusImages.community, campusImages.workshop, campusImages.performance];
  return <Link className={`featured-club featured-club-${index}`} to={`/clubs/${club.id}`}><img src={images[index]} alt={`${club.name} community`} /><div className="featured-club-body"><span className="club-category">{club.category || 'Campus community'}</span><h3>{club.name}</h3><p>{club.description}</p><span className="text-link">View profile <ArrowUpRight size={15} /></span></div></Link>;
}

function UpcomingEvent({ event, clubs }: { event: Event; clubs: Club[] }) {
  const club = clubs.find((item) => item.id === event.clubId);
  const date = new Date(event.startsAt);
  return <Link className="upcoming-editorial-row" to={`/events/${event.id}`}><div className="upcoming-date"><strong>{date.toLocaleDateString('en-IN', { day: '2-digit' })}</strong><span>{date.toLocaleDateString('en-IN', { month: 'short' })}</span></div><div className="upcoming-main"><span className="club-category">{club?.name || 'Campus event'} · {event.category || 'Event'}</span><h3>{event.title}</h3><p>{event.description}</p></div><div className="upcoming-meta"><span><CalendarDays size={14} />{date.toLocaleDateString('en-IN', { weekday: 'short' })}</span><span><MapPin size={14} />{event.venue || 'Venue to be announced'}</span><ArrowUpRight size={18} /></div></Link>;
}
